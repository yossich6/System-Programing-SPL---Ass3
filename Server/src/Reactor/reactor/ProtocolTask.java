package Reactor.reactor;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import Reactor.protocol.*;
import Reactor.tokenizer.*;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<StringMessage> implements Runnable {

	private final ServerProtocol<StringMessage> _protocol;
	private final MessageTokenizer<StringMessage> _tokenizer;
	private final ConnectionHandler<StringMessage> _handler;

	public ProtocolTask(final ServerProtocol<StringMessage> protocol, final MessageTokenizer<StringMessage> tokenizer, final ConnectionHandler<StringMessage> h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
		while (_tokenizer.hasMessage())
		{
			StringMessage msg = _tokenizer.nextMessage();
			System.out.println("Received \"" + msg + "\" from client");

			this._protocol.processMessage(msg,
					response -> {
						if (response != null) {
							try {
								ByteBuffer bytes = _tokenizer.getBytesForMessage(response);
								this._handler.addOutData(bytes);
							} catch (CharacterCodingException e) { e.printStackTrace(); }
						};
					});

			if (this._protocol.isEnd(msg))
			{
				break;
			}

		}
	}

	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}

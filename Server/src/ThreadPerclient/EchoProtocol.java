package ThreadPerclient;

class EchoProtocol implements ServerProtocol {
	
	public EchoProtocol() { }
		
	public boolean isEnd(String msg)
	{
		return msg.equals("bye");
	}

	@Override
	public void processMessage(Object msg, ProtocolCallback callback) {
		// TODO Auto-generated method stub
		
	}
}

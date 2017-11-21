package ThreadPerclient;

public interface ProtocolCallback<T> {

	void sendMessage(T msg) throws java.io.IOException;
	
	
}

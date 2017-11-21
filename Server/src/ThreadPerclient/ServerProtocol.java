package ThreadPerclient;

interface ServerProtocol<T> {
	
	void  processMessage(T msg , ProtocolCallback <T> callback);
	
	boolean isEnd(String msg);
	
}
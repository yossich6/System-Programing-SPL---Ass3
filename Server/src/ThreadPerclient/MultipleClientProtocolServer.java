package ThreadPerclient;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


import java.net.*;


class MultipleClientProtocolServer implements Runnable {
	private ServerSocket serverSocket;
	private int listenPort;
	private ServerProtocolFactory factory;
	
	
	public MultipleClientProtocolServer(int port, ServerProtocolFactory p)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (true)
		{
			try {
			ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
            new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
	}
	

	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		DataBase dataBase=DataBase.getInstance();
		Gson gson=new Gson();
		JsonReader reader=new JsonReader(new FileReader("json.json"));
		JsonFactory infoJson=gson.fromJson(reader, JsonFactory.class);
		reader.close();
		
		questions[] allQuestions = infoJson.questions;
		dataBase.getQuestions(allQuestions);
	
		// Get port
		int port = Integer.decode(args[0]).intValue();
		
		MultipleClientProtocolServer server = new MultipleClientProtocolServer(port, new EchoProtocolFactory());
		Thread serverThread = new Thread(server);
      serverThread.start();
		try {
			serverThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
					
	}
}

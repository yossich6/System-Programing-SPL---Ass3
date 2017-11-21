package ThreadPerclient;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;


//import java.util.concurrent.ConcurrentHashMap;

public class Room {
	Map<String,Integer> players;
	DataBase data=DataBase.getInstance();
	boolean started;
	
	
	public Game game;
	String roomName;
	
	public Room(String roomName,String playerName)
	{
		this.roomName=roomName;
		players=new ConcurrentHashMap<>();
		players.put(playerName, 0);
	}

	public void addPlayer(String name)
	{
		players.put(name, 0);
		
	}
	public void addScore(String name,Integer score)
	{
		Integer point= players.get(name);
		point=point+score;
	}
	public void remove(String name)
	{
		players.remove(name);
	}
	public boolean isStarted()
	{
		return started;
	}
	public void roomBroadcast(String msg,String senderName)
	{
		for ( String player : players.keySet() ) {
			try {
				if (senderName!=player){
				data.getPlayerCallBack(player).sendMessage("USRMSG "+senderName+": "+msg.substring(4));
				}} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
	}
	public void roomMessage(String msg)
	{
		for ( String player : players.keySet() ) {
			try {
			
				data.getPlayerCallBack(player).sendMessage(msg);
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
	}
	public boolean gameStarted()
	{
		return started;
	}
	public void startBluffer()
	{
		started=true;
		game=new Bluffer(players,roomName);
		game.StartGame();
	}

	public boolean moreThatOnePlayer(){
		boolean ans;
		if(players.size() >1) ans=true;
		else ans=false;;
		return ans;
	}

	public boolean canSend(String msg) {
		return game.canSend(msg);		
	}
	

}


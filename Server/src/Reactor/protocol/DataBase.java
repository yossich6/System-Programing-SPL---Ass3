package Reactor.protocol;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Reactor.tokenizer.StringMessage;


public class DataBase {

	//List<String> players=new LinkedList();
	Map<String,String> players=new ConcurrentHashMap<>();
	public Map<String,ProtocolCallback> playersCall=new ConcurrentHashMap<>();
	Map<String,Room> rooms=new ConcurrentHashMap<>();
	questions[] gameQuestions;
	Set<String> knownGames = new LinkedHashSet<String>();
	
	private DataBase(){
		knownGames.add("BLUFFER");
	}

	private static class SingletonHolder {
		private static DataBase instance = new DataBase();
		
	}

	public static DataBase getInstance() {
		return SingletonHolder.instance;
	}

	public void addPlayer(String player)
	{
		players.put(player, "");
	}
	public void addPlayerCallBack(String player,ProtocolCallback call)
	{
		playersCall.put(player, call);
	}
	public ProtocolCallback getPlayerCallBack(String player)
	{
		return playersCall.get(player);
	}
	public void addRoom(String room,String player)
	{
		Room roomy=new Room(room,player);
		rooms.put(room, roomy);
		System.out.println("The number of players in Room "+room+"is: "+ rooms.size());
	}
	public void delateRoom(String room)
	{
		rooms.remove(room);
		
	}
	public boolean containsPlayer(String player)
	{
		return players.containsKey(player);
	}
	public void setPlayerRoom(String player,String room)
	{
		players.remove(player);
		players.put(player, room);
	}
	public boolean containsRoom(String room)
	{
		return rooms.containsKey(room);
	}
	public Room getRoom(String room)
	{
		return rooms.get(room);
	}
	public String getPlayerRoom(String player)
	{
		return players.get(player);
	}
	public String roomList()
	{
		String ans="";
		for ( String key : rooms.keySet() ) {
		    ans=ans+" | "+key;
		}
		return ans;
		
	}

	public void getQuestions(questions[] allQuestions) {
		gameQuestions=allQuestions;
		
	}
	public void privateMessage(String msg,String name) {
		 try {
			 StringMessage newMsg=new StringMessage(msg);
			playersCall.get(name).sendMessage(newMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public questions[] randomThreeQuestions(){
		questions[] threeRandomQuestions=new questions[3];
		Random rand=new Random();
		
		int number0= rand.nextInt(gameQuestions.length);
		threeRandomQuestions[0]=gameQuestions[number0];
		
		int number1=rand.nextInt(gameQuestions.length);
		while(number1 == number0){
			number1=rand.nextInt(gameQuestions.length);
		}
		threeRandomQuestions[1]=gameQuestions[number1];

		
		int number2=rand.nextInt(gameQuestions.length);
		while(number2 == number0 || number2==number1 ){
			number2=rand.nextInt(gameQuestions.length);
		}
		threeRandomQuestions[2]=gameQuestions[number2];

		return threeRandomQuestions;
	}

	public String listGames() {
		String ans="";
		for(String games: knownGames){
			ans=ans+" "+games;
		}
		return ans;
	}

	public boolean containsGame(String msg) {
		return knownGames.contains(msg);
	}

}



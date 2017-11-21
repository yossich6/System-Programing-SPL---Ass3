package ThreadPerclient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;



public class Bluffer implements Game {
	Map<String,Integer> playersAndScores;
	Map<String,LinkedList<String>> fakeansAnswers;
	Map<Integer,LinkedList<String>> realAnswers;
	Map<Integer,String> convertAns;
	Map<String,Integer> pointsAtRound;
	Map<String,Boolean> correctAtRound;
	int fakeAnswersCounter;
	int playersAnswersCounter;
	DataBase dataBase=DataBase.getInstance();
	String correctAnswer;
	questions[] theGameQuestions;
	boolean gameOver=false;
	String roomName;
	
	int counter=0;
	int correctAnswerPlace=0;
	Random rand=new Random();
	int round=0;


	public Bluffer(Map<String,Integer> players,String roomName){
		this.playersAndScores=players;
		this.roomName=roomName;
	}


	public void gameMessage(String msg)
	{
		for ( String player : playersAndScores.keySet() ) {
			try {
			
				dataBase.getPlayerCallBack(player).sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void StartGame() {
		theGameQuestions=dataBase.randomThreeQuestions();

		
			convertAns=new ConcurrentHashMap<Integer,String>();
			fakeansAnswers=new ConcurrentHashMap<String,LinkedList<String>>();
			realAnswers=new ConcurrentHashMap<Integer,LinkedList<String>>();
			pointsAtRound=new ConcurrentHashMap<String,Integer>();
			correctAtRound=new ConcurrentHashMap<String,Boolean>();
			for ( String player : playersAndScores.keySet() )
			{
				pointsAtRound.put(player, 0);
				correctAtRound.put(player, false);
			}

			counter=0;
			correctAnswerPlace=0;
			fakeAnswersCounter=0;
			playersAnswersCounter=0;
			Random rand=new Random();

			correctAnswer=theGameQuestions[round].getRealAnswer().toLowerCase();

			gameMessage("ASKTXT "+theGameQuestions[round].getQuestionText());
			
	}
	
	public void endFake(){
		
		
		correctAnswerPlace=rand.nextInt(fakeansAnswers.size());//the random place where the correct answer will be

		String sendingFakeAnswers="ASKCHOICES ";

		for ( String ans : fakeansAnswers.keySet() ) { //making the question string
			if(counter==correctAnswerPlace){ //if this is the place that the right answer should be
				sendingFakeAnswers=sendingFakeAnswers+counter+". "+correctAnswer+" ";//adding the answer to the question
				convertAns.put(correctAnswerPlace, correctAnswer); 
				
				counter++;
			}

			sendingFakeAnswers=sendingFakeAnswers+counter+". "+ans+" ";//adding the answer to the question
			convertAns.put(counter, ans);
			
			counter++;


		}
		sendingFakeAnswers.toLowerCase();
		gameMessage(sendingFakeAnswers);
		
		
	}
	
	public void endAnswers(){
		gameMessage("The Correct Answer is: "+correctAnswer);

		for ( Integer ans : realAnswers.keySet() ) {
			if (ans==correctAnswerPlace){ //, give the man who gave the answer +10
			
				LinkedList<String> correctAnswerPlayers=realAnswers.get(ans);//getting all the people who said the right ans
			
				for ( String player : correctAnswerPlayers ) { //giving all the players 10 points
					int score=pointsAtRound.get(player)+10;
					//pointsAtRound.remove(player);
					pointsAtRound.put(player, score);
					//correctAtRound.remove(player);
					correctAtRound.put(player, true);
				}

			}
			else
			{
				int numOfPoints=realAnswers.get(ans).size()*5; //the amount of people chose these answer*5
				LinkedList<String> fakeWinners=fakeansAnswers.get(convertAns.get(ans));
				for ( String player : fakeWinners ) { //giving all the players gave that wrong answer the points
					int score=pointsAtRound.get(player)+numOfPoints; //update the points
					pointsAtRound.remove(player);
					pointsAtRound.put(player, score);

				}

			}
		}
		for ( String player : pointsAtRound.keySet() )
		{
			playersAndScores.put(player,playersAndScores.get(player)+pointsAtRound.get(player));
			String ans;
			if (correctAtRound.get(player)==true) ans="Correct! ";
			else ans="Wrong! ";

			dataBase.privateMessage(ans+"+"+pointsAtRound.get(player), player);
		}
		String lastmsg="Summary: ";
		for ( String player : playersAndScores.keySet() ){
			lastmsg=lastmsg+player+":"+playersAndScores.get(player)+" ";
		}
		gameMessage(lastmsg);
		if (round==2)	endOfGame();
		else
		{ anotherRound();}

	}
	
	public void anotherRound(){
		round++;
		convertAns=new ConcurrentHashMap<Integer,String>();
		fakeansAnswers=new ConcurrentHashMap<String,LinkedList<String>>();
		realAnswers=new ConcurrentHashMap<Integer,LinkedList<String>>();
		pointsAtRound=new ConcurrentHashMap<String,Integer>();
		correctAtRound=new ConcurrentHashMap<String,Boolean>();
		

		for ( String player : playersAndScores.keySet() )
		{
			pointsAtRound.put(player, 0);
			correctAtRound.put(player, false);
		}

		counter=0;
		correctAnswerPlace=0;
		fakeAnswersCounter=0;
		playersAnswersCounter=0;
		Random rand=new Random();

		correctAnswer=theGameQuestions[round].getRealAnswer().toLowerCase();

		gameMessage("ASKTXT "+theGameQuestions[round].getQuestionText());
		
	}
	
	public void endOfGame(){
		dataBase.delateRoom(roomName);
		for ( String player : pointsAtRound.keySet() )
		{
			dataBase.setPlayerRoom(player, "");
		}
		
	}
	
	
	@Override
	public void getMessage(String msg,String name) {
		if( msg.startsWith("TXTRESP")){	
			String fakeans=msg.substring(8).toLowerCase();
			if(fakeans.equals(correctAnswer))
			{
				dataBase.privateMessage("SYSMSG This is the correct answer, please make up another answer", name);
			}
			else
			{
				if(fakeansAnswers.containsKey(fakeans)){
					fakeansAnswers.get(fakeans).add(name);
				}
				else{
					LinkedList<String> newName=new LinkedList<String>();
					newName.add(name);
					fakeansAnswers.put(fakeans, newName);
				
				}

				fakeAnswersCounter++;
			}
			if (fakeAnswersCounter==playersAndScores.size()) endFake();
			
		}

		if( msg.startsWith("SELECTRESP")){
			Integer theAnswer=Integer.parseInt(msg.substring(11));
			if(realAnswers.containsKey(theAnswer)){
			
				realAnswers.get(theAnswer).add(name);
			}
			else{
				LinkedList<String> newName=new LinkedList<String>();
				newName.add(name);
				Integer number=Integer.parseInt(msg.substring(11));
				realAnswers.put(number, newName);
			}
			playersAnswersCounter++;
			
			if (playersAnswersCounter==playersAndScores.size()) endAnswers();
		}


	}


	@Override
	public boolean canSend(String msg) {
		boolean ans = false;
		if( msg.startsWith("SELECTRESP")){
			try{
				Integer helper=Integer.parseInt(msg.substring(11));
				if(helper.intValue()>=0 && helper.intValue()<=fakeansAnswers.size())
					ans= true;
				else ans=false;
			}catch (NumberFormatException e){ ans= false; };
		}
		return ans;
	}
	
}

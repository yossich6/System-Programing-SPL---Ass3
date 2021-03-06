package ThreadPerclient;

import java.io.IOException;


public class GameServerProtocol implements ServerProtocol<String>{

	private String name="";
	boolean nickFlag=false;
	boolean joinFlag=false;
	boolean txtFlag=false;
	DataBase dataBase=DataBase.getInstance();

	public String getPlayer(){
		return name;
	}


	@Override
	public void processMessage(String msg, ProtocolCallback<String> callback) {
		String callbackMessage="";	

		if(msg.startsWith("NICK")){
			int num=((String) msg).indexOf(" ");
			String helper=((String) msg).substring(num+1);
			if(name=="")
			{

				if(dataBase.containsPlayer(helper)){
					callbackMessage="SYSMSG NICK REJECTED, PLEASE ENTER WITH OTHER NAME";
				}else{
					dataBase.addPlayer(helper);
					dataBase.addPlayerCallBack(helper, callback);
					name=helper;
					nickFlag=true;
					callbackMessage="SYSMSG NICK ACCEPTED";
				}
			}
			else
			{
				callbackMessage="SYSMSG NICK REJECTED, YOU ARE ALLREADY REGISTERED";			
			}
		}

		if(msg.startsWith("JOIN")){
			if(nickFlag){
				if(!checkIfStartGame(name)){
					int num=msg.indexOf(" ");
					String helper=msg.substring(num+1);
					if(dataBase.containsRoom(helper))
					{
						dataBase.setPlayerRoom(name,helper);
						Room current=dataBase.getRoom(helper);
						if(!current.isStarted())
						{
							current.addPlayer(name);
							joinFlag=true;
							callbackMessage="SYSMSG JOIN ACCEPTED";
						}
						else
						{
							callbackMessage="SYSMSG JOIN REJECTED, The game already started";
						}
					}
					else
					{
						dataBase.setPlayerRoom(name,helper);
						dataBase.addRoom(helper, name);
						joinFlag=true;
						callbackMessage="SYSMSG JOIN ACCEPTED";
					}

				} else callbackMessage="SYSMSG JOIN REJECTED, YOU CAN'T LEAVE IN THE MIDDLE OF THE GAME";
			} 

			else callbackMessage="SYSMSG JOIN REJECTED, YOU HAVE TO WRITE YOUR NAME";
		}

		if(msg.startsWith("MSG ")){
			if(nickFlag && joinFlag){
				String roomName=dataBase.getPlayerRoom(name);
				if(dataBase.getRoom(roomName).moreThatOnePlayer()){
					dataBase.getRoom(roomName).roomBroadcast(msg, name);
					callbackMessage="SYSMSG JOIN ACCEPTED";
				}
				else callbackMessage="MSG REJECTED, YOU ARE ALONE IN THE ROOM";
			}
			else callbackMessage="MSG REJECTED, YOU HAVE TO WRITE YOUR NAME AND JOIN ROOM";
		}



		if(msg.startsWith("LISTGAMES")){
			callbackMessage="SYSMSG LISTGAMES ACCEPTED"+ dataBase.listGames();

		}

		if(msg.startsWith("STARTGAME ")){
			if(!checkIfStartGame(name)){
				if(nickFlag && joinFlag){
					if(msg.length()>10 && dataBase.containsGame((msg.substring(10)))){
						String roomName=dataBase.getPlayerRoom(name);
						System.out.println("trying to get to room "+roomName);
						if(msg.substring(10).equals("BLUFFER")){
							dataBase.getRoom(roomName).startBluffer();
							callbackMessage="SYSMSG STARTGAME ACCEPTED";
						}
					}
					else callbackMessage="SYSMSG STARTGAME REJECTED, WE DON'T HAVE SUCH GAME";

				}
				else callbackMessage="SYSMSG STARTGAME REJECTED, YOU HAVE TO WRITE YOUR NAME AND JOIN ROOM";
			} else callbackMessage="SYSMSG STARTGAME REJECTED, YOU ARE IN A MIDDLE OF A GAME!!";
		}


		if(msg.startsWith("TXTRESP ")){
			if(nickFlag && joinFlag && checkIfStartGame(name) && !txtFlag){
				String roomName=dataBase.getPlayerRoom(name);
				dataBase.getRoom(roomName).game.getMessage(msg, name);
				txtFlag=true;
				callbackMessage="SYSMSG TXTRESP ACCEPTED";

			}
			else callbackMessage="SYSMSG TXTRESP REJECTED";
		}


		if(msg.startsWith("SELECTRESP ")){
			if(nickFlag && joinFlag && checkIfStartGame(name) && txtFlag){
				String roomName=dataBase.getPlayerRoom(name);
				if(dataBase.getRoom(roomName).canSend(msg)){

					dataBase.getRoom(roomName).game.getMessage(msg, name);
					txtFlag=false;
					callbackMessage="SYSMSG SELECTRESP ACCEPTED";
				} else callbackMessage="SYSMSG SELECTRESP REJECTED, PLEASE ENTER CORRECT NUMBERS";
			}
			else callbackMessage="SYSMSG SELECTRESP REJECTED";
		}

		if(msg.startsWith("QUIT")){
			if(!nickFlag || !joinFlag ) callbackMessage="SYSMSG QUIT ACCEPTED";
			else{
				if(dataBase.getPlayerRoom(name)!="")
				{
					if(joinFlag && dataBase.getRoom(dataBase.getPlayerRoom(name)).gameStarted()) 
						callbackMessage="SYSMSG QUIT REJECTED";
					else callbackMessage="SYSMSG QUIT ACCEPTED";
				}
				else callbackMessage="SYSMSG QUIT ACCEPTED";
			}
		}


		if(!msg.startsWith("NICK ") && !msg.startsWith("JOIN ") && !msg.startsWith("MSG ")
				&& !msg.startsWith("LISTGAMES ") && !msg.startsWith("STARTGAME ") && !msg.startsWith("TXTRESP ")
				&& !msg.startsWith("SELECTRESP ") && !msg.startsWith("QUIT")){
			callbackMessage="SYSMSG " +msg+ " UNIDENTIFIED";
		}

		try {
			callback.sendMessage(callbackMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean checkIfStartGame(String player) {
		boolean ans;
		if(joinFlag){
			Room current= dataBase.getRoom(dataBase.getPlayerRoom(player));
			if(current.isStarted())
				ans=true;
			else ans=false;
		}else ans=false;
		return ans;
	}

	@Override
	public boolean isEnd(String msg) {
		if(msg.equals("QUIT"))
		{
			if(dataBase.getPlayerRoom(name)!="") 
			{
				if(joinFlag && dataBase.getRoom(dataBase.getPlayerRoom(name)).gameStarted())
					return false; //if the room already started to play

				else return true; //if the room didnt started to play
			}
			else return true;
		}
		else return false; //im not in a room


	}
}

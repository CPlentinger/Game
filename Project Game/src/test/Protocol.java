package test;
/**
 * The Class Protocol implements the protocol and provides functions to help implementing
 * the protocol. This protocol is for Group 7, Mod02 BIT/INF 2016/2017, UTWENTE
 */
public class Protocol {

  /**
   * Server to client messages.
   * @author Merel Meekes
   */
  public class Server {

    public static final String SERVERCAPABILITIES = "serverCapabilities";

    public static final String SENDLISTROOMS = "sendListRooms";

    public static final String ASSIGNID = "assignID";

    public static final String STARTGAME = "startGame";

    public static final String TURNOFPLAYER = "playerTurn";

    public static final String NOTIFYMOVE = "notifyMove";

    public static final String NOTIFYEND = "notifyEnd";

    public static final String ERROR = "error";

    public static final String NOTIFYMESSAGE = "notifyMessage";

    public static final String SENDLEADERBOARD = "sendLeaderBoard";

  }

  /**
   * Client to server messages.
   * @author Merel Meekes
   */
  public static class Client {

    public static final String SENDCAPABILITIES = "sendCapabilities";

    public static final String JOINROOM = "joinRoom";

    public static final String GETROOMLIST = "getRoomList";

    public static final String LEAVEROOM = "leaveRoom";

    public static final String MAKEMOVE = "makeMove";

    public static final String SENDMESSAGE = "sendMessage";

    public static final String REQUESTLEADERBOARD = "requestLeaderboard";

  }

  /**
   * Function to get Error message by error code defined in protocol.
   * @param number String with error code defined in Protocol
   * @return Error explanation or null if invalid error code.
   */
  public static String getError(String number) {
    String result = null;
    if (number.equals("1")) {
      result = "Client has not yet sent capabilities message, Server cannot proceed";
    } else if (number.equals("2")) {
      result = "Room sent in message joinRoom does not exist";
    } else if (number.equals("3")){
      result = "The chosen room is no longer available, either it already filled up or was empty for too long";
    } else if (number.equals("4")){
      result = "The input given by the client isn�t valid at this moment";
    } else if (number.equals("5")){
      result = "The given move is not possible on this board";
    } else if (number.equals("6")){
      result = "Client is not allowed to leave the room after the game has started";
    } else if (number.equals("7")){
      result = "A message with piping in a wrong place was received";
    } else {
      result = "unknown error";
    }

    return result;
  }

  public static String getWin(String number){
    String result = null;
    if(number.equals("1")){
      result = "The game was won!";
    } else if(number.equals("2")){
      result = "Draw! The board is full.";
    } else if(number.equals("3")){
      result = "Player disconnected. The game cannot continue.";
    } else if(number.equals("4")){
      result = "Player didn't respond. The game cannot continue.";
    } else {
      result = "unknown win condition";
    }

    return result;

  }

}
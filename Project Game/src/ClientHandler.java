

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {

  private Socket server;
  private BufferedReader in;
  private BufferedWriter out;
  private Controller clientGame;
  
  
  public ClientHandler(Socket sock) throws IOException {
    this.server = sock;
    this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    this.clientGame = new Controller();
    clientGame.setName(clientGame.getResponse("Choose your username: "));
  }
  
  public void run() {
    readInput();
  }

  public void readInput() {
    String message;
    try {
      while ((message = in.readLine()) != null) {
            handleInput(message);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void handleInput(String message) {
    Scanner inScanner = new Scanner(message);
    switch (inScanner.next()) {
      case Protocol.Server.SERVERCAPABILITIES:
        writeOutput(Protocol.Client.SENDCAPABILITIES + clientGame.getName() + " 0 4 4 4 4 0 0");
        break;
      case Protocol.Server.STARTGAME: startGame(message);
        break;
      case Protocol.Server.TURNOFPLAYER:
        if (inScanner.nextInt() == clientGame.getID()) {
          makeMove();
        } else {
          System.out.println("Waiting for opponents turn...");
        }
        break;
      case Protocol.Server.NOTIFYMOVE: 
        if (inScanner.nextInt() == clientGame.getID()) {
          clientGame.board.setField(inScanner.nextInt(), inScanner.nextInt(), clientGame.getMark());
        } else {
          clientGame.board.setField(inScanner.nextInt(), inScanner.nextInt(), clientGame.getMark().Other());
        }
        break;
      case Protocol.Server.NOTIFYEND: handleEnd(message);
        break;
      default: System.out.println(message);
        break;
    }
  }
  
  private void handleEnd(String endMessage) {
    String winCode = endMessage.split(" ")[1];
    if (winCode.equals("1")) {
      String playerid = endMessage.split(" ")[2];
      if (playerid.equals(String.valueOf(clientGame.getID()))) {
        System.out.println(Protocol.getWin("1") + " Congratulations!");
      } else {
        System.out.println(Protocol.getWin("1") + " Better luck next time.");
      }
    } else if (winCode.equals("2")) {
      System.out.println(Protocol.getWin("2"));
    } else if (winCode.equals("3")) {
      String playerid = endMessage.split(" ")[2];
      System.out.println(Protocol.getWin("3"));
      if (playerid.equals(String.valueOf(clientGame.getID()))) {
        System.out.println("You lost the game. Better luck next time.");
      }
    } else if (winCode.equals("4")) {
      String playerid = endMessage.split(" ")[2];
      System.out.println(Protocol.getWin("4"));
      if (playerid.equals(String.valueOf(clientGame.getID()))) {
        System.out.println("You lost the game. Better luck next time.");
      } else {
        System.out.println(Protocol.getWin("1") + " Congratulations!");
      }
    } else {
      System.out.println(Protocol.getWin("unknown"));
    }
  }

  private void makeMove() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Player ");
    stringBuilder.append(clientGame.getName());
    stringBuilder.append(" (");
    stringBuilder.append(clientGame.getMark().toString());
    stringBuilder.append("), ");
    stringBuilder.append("make your move (i.e.: x y):");
    String move = clientGame.getResponse(stringBuilder.toString());
    writeOutput(Protocol.Client.MAKEMOVE + " " + move);
  }
  
  public boolean confirmMove() {
    String message = "";
    try {
      message = in.readLine();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (message.startsWith(Protocol.Server.NOTIFYMOVE)) {
      return true;
    } else {
      return false;
    }
  }
  public void writeOutput(String message) {
      try {
        out.write(message);
        out.newLine();
        out.flush();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }
  
  public void startGame(String message) {
    Scanner scan = new Scanner(message);
    String playerInfo = "";
    while (scan.hasNext()) {
      playerInfo = scan.next();
      if (playerInfo.contains(clientGame.getName())) {
        break;
      }
    }
    clientGame.setID(Integer.valueOf(playerInfo.split("\\|")[0]));
    if (playerInfo.split("\\|")[2].equals("ff0000")) {
      clientGame.setMark(Mark.X);
    } else {
      clientGame.setMark(Mark.O);
    }
    clientGame.buildBoard(message.substring(10,15));
  }
}

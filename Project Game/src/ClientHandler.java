

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler extends Thread {

  private Socket server;
  private BufferedReader in;
  private BufferedWriter out;
  private HumanPlayer clientGame;
  private String type;
  
  public ClientHandler(Socket sock, String username, String type) throws IOException {
    this.server = sock;
    this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    this.type = type;
    if (type.equals("Computer")) {
      this.clientGame = new ComputerPlayer();
    } else {
      this.clientGame = new HumanPlayer();
    }
    this.clientGame.setName(username);
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
    } catch (SocketException e) {
      handleEnd(Protocol.Server.NOTIFYEND + " 4 " + clientGame.getID());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void handleInput(String message) {
    Scanner inScanner = new Scanner(message);
    switch (inScanner.next()) {
      case Protocol.Server.SERVERCAPABILITIES:
        writeOutput(Protocol.Client.SENDCAPABILITIES + " 2 " + clientGame.getName() + " 0 4 4 4 4 0 0");
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
          clientGame.makeMove(inScanner.nextInt(), inScanner.nextInt(), clientGame.getMark());
        } else {
          clientGame.makeMove(inScanner.nextInt(), inScanner.nextInt(), clientGame.getMark().Other());
        }
        break;
      case Protocol.Server.NOTIFYEND:
        handleEnd(message);
        break;
      default: System.out.println(message);
        break;
    }
    inScanner.close();
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
      System.out.println("\n" + Protocol.getWin("4"));
      if (playerid.equals(String.valueOf(clientGame.getID()))) {
        System.out.println("You lost the game. Better luck next time.");
      } else {
        System.out.println(Protocol.getWin("1") + " Congratulations!");
      }
    } else {
      System.out.println(Protocol.getWin("unknown"));
    }
    String nextGame = clientGame.getResponse("Do you want to play another game? (y/n)");
    if (nextGame.equals("y")) {
      try {
        new ClientHandler(new Socket(server.getInetAddress(), server.getPort()),clientGame.getName(), type).start();
        this.interrupt();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      writeOutput(Protocol.Server.NOTIFYEND + " 3 " + clientGame.getID());
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
    writeOutput(Protocol.Client.MAKEMOVE + " " + clientGame.getResponse(stringBuilder.toString()));
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
      System.out.println(message);
      try {
        out.write(message);
        out.newLine();
        out.flush();
      } catch (SocketException e) {
        handleEnd(Protocol.Server.NOTIFYEND + " 4 " + clientGame.getID());
      } catch (IOException e) {
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

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
  private Player player;
  private Player hintBot;
  
  public ClientHandler(Socket sock, String username, Player p) throws IOException {
    this.server = sock;
    this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    this.player = p;
    player.setName(username);
    this.hintBot = new ComputerPlayer(1);
  }
  
  public void run() {
    System.out.println("Waiting for opponent...");
    readInput();
  }

  public void readInput() {
    String message;
    try {
      while ((message = in.readLine()) != null) {
        handleInput(message);
      }
    } catch (SocketException e) {
      handleEnd(Protocol.Server.NOTIFYEND + " 4 " + player.getID());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void handleInput(String message) {
    Scanner inScanner = new Scanner(message);
    switch (inScanner.next()) {
      case Protocol.Server.SERVERCAPABILITIES:
        writeOutput(Protocol.Client.SENDCAPABILITIES + " 2 " + player.getName() + " 0 4 4 4 4 0 0");
        break;
      case Protocol.Server.ASSIGNID:
        player.setID(inScanner.nextInt());
        hintBot.setID(player.getID());
        break;
      case Protocol.Server.STARTGAME: startGame(message);
        break;
      case Protocol.Server.TURNOFPLAYER:
        if (inScanner.nextInt() == player.getID()) {
          askMove();
        } else {
          System.out.println("Waiting for player " + player.getOpponentName() + " (" + player.getMark().Other().toString() + ")...");
        }
        break;
      case Protocol.Server.NOTIFYMOVE: 
        setMove(message);
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
    String playerid = endMessage.split(" ")[2];
    switch (winCode) {
      case "1":
        if (playerid.equals(String.valueOf(player.getID()))) {
          System.out.println(Protocol.getWin("1") + " Congratulations " + player.getName() + "!");
        } else {
          System.out.println("You lost the game. Better luck next time.");
        }
        break;
      case "2":
        System.out.println(Protocol.getWin("2"));
        break;
      case "3":
        System.out.println(Protocol.getWin("3"));
        if (playerid.equals(String.valueOf(player.getID()))) {
          System.out.println("You lost the game. Better luck next time.");
        } else {
          System.out.println(Protocol.getWin("1") + " Congratulations " + player.getOpponentName() + "!");
        }
        break;
      case "4":
        System.out.println("\n" + Protocol.getWin("4"));
        if (playerid.equals(String.valueOf(player.getID()))) {
          System.out.println("You lost the game. Better luck next time.");
        } else {
          System.out.println(Protocol.getWin("1") + " Congratulations " + player.getOpponentName() + "!");
        }
      default: break;
    }
    String nextGame = player.getMove("Do you want to play another game? (y/n)");
    if (nextGame.equals("y")) {
      try {
        new ClientHandler(new Socket(server.getInetAddress(), server.getPort()),player.getName(), player).start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Game ended, thanks for playing!");
      System.exit(0);
    }
  }

  private void askMove() {
    if (player instanceof HumanPlayer) {
      hintBot.getController().setBoard(player.getController().getBoard());
      System.out.println(hintBot.getMove("hint: "));
    }
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Player ");
    stringBuilder.append(player.getName());
    stringBuilder.append(" (");
    stringBuilder.append(player.getMark().toString());
    stringBuilder.append("), ");
    stringBuilder.append("make your move (i.e.: x y):");
    String move = player.getMove(stringBuilder.toString());
    if (player instanceof ComputerPlayer) {
      System.out.print(move);
    }
    int xpos = Integer.parseInt(move.substring(0,1));
    int ypos = Integer.parseInt(move.substring(2));
    if (player.checkMove(xpos, ypos)) {
      writeOutput(Protocol.Client.MAKEMOVE + " " + move);
    } else {
      System.out.println("Please choose a empty field on the board!");
      askMove();
    }
    
  }
  
  public void setMove(String message) {
    int xpos = Integer.parseInt(message.split(" ")[2]);
    int ypos = Integer.parseInt(message.split(" ")[3]);
    if (message.split(" ")[1].equals(String.valueOf(player.getID()))) {
      player.makeMove(xpos, ypos, player.getMark());
    } else {
      player.makeMove(xpos, ypos, player.getMark().Other());
    }
  }
  
  public void writeOutput(String message) {
      try {
        out.write(message);
        out.newLine();
        out.flush();
      } catch (SocketException e) {
        handleEnd(Protocol.Server.NOTIFYEND + " 4 " + player.getID());
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
  
  public void startGame(String message) {
    System.out.println("Starting new game.");
    String[] players = message.substring(18).split(" ");
    for (String playerInfo : players) {
      if (playerInfo.startsWith(String.valueOf(player.getID()))) {
        if (playerInfo.split("\\|")[2].equals("ff0000")) {
          player.setMark(Mark.X);
          hintBot.setMark(Mark.X);
        } else {
          player.setMark(Mark.O);
          hintBot.setMark(Mark.O);
        }
      } else {
        player.setOpponentName(playerInfo.split("\\|")[1]);
      }
    }
    player.buildBoard(message.substring(10,15));
  }
}

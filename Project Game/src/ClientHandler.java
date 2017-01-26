

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ClientHandler extends Thread implements Observer {

  private Socket server;
  private BufferedReader in;
  private BufferedWriter out;
  private int id;
  private String name;
  private Mark m;
  private TUIView view;
  private Controller clientGame;
  
  
  public ClientHandler(Socket sock) throws IOException {
    this.server = sock;
    this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    this.view = new TUIView();
  }
  
  public void run() {
    this.name = view.getResponse("Choose your username: ");
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
      case Protocol.Server.SERVERCAPABILITIES: writeOutput(Protocol.Client.SENDCAPABILITIES + name + " 0 4 4 4 4 0 0");
        break;
      case Protocol.Server.STARTGAME: startGame(message);
        break;
      case Protocol.Server.TURNOFPLAYER:
        if (inScanner.nextInt() == id) {
          makeMove();
        } else {
          System.out.println("Opponents turn");
        }
        break;
    }
  }
  
  private void makeMove() {
    System.out.println(1);
    String move = view.getResponse("Make your move (i.e.: x y)");
    int x = Integer.parseInt(move.substring(0, 1));
    int y = Integer.parseInt(move.substring(2));
    writeOutput(Protocol.Client.MAKEMOVE + " " +move);
    if (confirmMove()) {
      clientGame.board.setField(x, y, Mark.X);
      
    } else {
      System.out.println("Invalid Move");
      makeMove();
    }
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
    System.out.println(message);
    Scanner scan = new Scanner(message);
    String playerInfo = "";
    while (scan.hasNext()) {
      playerInfo = scan.next();
      if (playerInfo.contains(name)) {
        break;
      }
      
    }
    id = Integer.valueOf(playerInfo.split("\\|")[0]);
    if (playerInfo.split("\\|")[2].equals("ff0000")) {
      m = Mark.X;
    } else {
      m = Mark.O;
    }
    clientGame = new Controller(message.substring(10,15));
    clientGame.board.addObserver(this);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    view.view(clientGame.board);
  }
}

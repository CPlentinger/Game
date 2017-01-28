

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
  private static final String USAGE = "Usage: <address> <port>";
  
  public static void main(String[] args) throws IOException {
    InetAddress adrs = InetAddress.getLocalHost();
    int port = 2727;
    Socket socket = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    boolean infoSet = false;
    
    while (infoSet == false) {
      System.out.print("Please input an server address and port to connect to (i.e <address> <port>): ");
      try {
        String input = in.readLine();
        adrs = InetAddress.getByName(input.split(" ")[0]);
        port = Integer.valueOf(input.split(" ")[1]);
        infoSet = true;
      } catch (UnknownHostException e) {
        System.out.println("Input doesn't contain a valid server address.");
        System.out.println(USAGE);
      } catch (NumberFormatException e) {
        System.out.println("Input doesn't contain a valid port number.");
        System.out.println(USAGE);
      }
    }
    System.out.print("Please choose a username: ");
    String username = in.readLine();
    System.out.print("Do you want to start the computer player? (y/n)");
    String player = in.readLine();
    
    System.out.println("Connecting to:" + adrs + ":" + port);
    if (player.equals("y")) {
      
      ClientHandler client = new ClientHandler(new Socket(adrs, port), username, "Computer");
      client.start();
    } else {
      ClientHandler client = new ClientHandler(new Socket(adrs, port), username, "Human");
      client.start();
    }
    
  }

}

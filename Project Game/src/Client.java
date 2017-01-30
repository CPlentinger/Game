

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
  private static final String USAGE = "Usage: <address> <port>";
  
  public static void main(String[] args) throws IOException {
    InetAddress adrs = InetAddress.getLocalHost();
    int port = 2727;
    Socket socket = null;
    ClientHandler client = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    boolean infoSet = false;
      while (infoSet == false) {
        System.out.print("Please input an server address and port to connect to (i.e <address> <port>): ");
        try {
          String input = in.readLine();
          adrs = InetAddress.getByName(input.split(" ")[0]);
          port = Integer.valueOf(input.split(" ")[1]);
          socket = new Socket(adrs, port);
          infoSet = true;
        } catch (UnknownHostException | NoRouteToHostException e) {
          System.out.println("Input doesn't contain a valid server address.");
          System.out.println(USAGE);
        } catch (ConnectException e) {
          System.out.println("Couldn't connect to server.");
          System.out.println(USAGE);
          System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
          System.out.println("Input doesn't contain a valid port number.");
          System.out.println(USAGE);
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Input doesn't contain a server address and port number.");
          System.out.println(USAGE);
        }
      }
      System.out.print("Please choose a username: ");
      String username = in.readLine();
      System.out.print("Do you want to start the computer player? (y/n)");
      String player = in.readLine();
      try {
        if (player.equals("y")) {
          System.out.print("Choose a maximum thinking time in seconds: ");
          double time = Double.parseDouble(in.readLine());
          System.out.println("Connecting to:" + adrs + ":" + port);
          client = new ClientHandler(socket, username, new ComputerPlayer(time));
          client.start();
        } else {
          System.out.println("Connecting to:" + adrs + ":" + port);
          client = new ClientHandler(socket, username, new HumanPlayer());
          client.start();
        }
      } catch (NoRouteToHostException e) {
        System.out.println("couldn't connect to host.");
      }
  }

}

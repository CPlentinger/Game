package project.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

import project.game.ComputerPlayer;
import project.game.HumanPlayer;
import project.game.Mark;

public class Client {
  private static final String USAGE = "Usage: <address> <port>";
  
  /**
   * Connect the <code>client</code> to the <code>server</code> with the connection details from the console and start a <code>clientHandler</code>.
   * the server address and port are read from the console using a <code>BufferedReader</code>.
   * If these are correct a connection is made. Otherwise, errors are handled and the user is asked again for input.
   * After that, the user inputs a <code>username</code> and specifies which kind of <code>Player</code> it wants to use.
   * In case of a <code>ComputerPlayer</code>, the user is asked for <code>thinkingTime</code>.
   * A new <code>Thread</code> of <code>ClientHandler</code> is started.
   * @param args, isn't used.
   */
  public static void main(String[] args) {
    InetAddress adrs = null;
    int port = 2727;
    Socket socket = null;
    String username = "";
    String player = "";
    ClientHandler client = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    boolean infoSet = false;
    boolean started = false;
      while (infoSet == false) {
        System.out.print("Please input an server address and port to connect to (i.e <address> <port>): ");
        try {
          String input = in.readLine();
          adrs = InetAddress.getByName(input.split(" ")[0]);
          port = Integer.valueOf(input.split(" ")[1]);
          socket = new Socket(adrs, port);
          infoSet = true;
          
          System.out.print("Please choose a username: ");
          username = in.readLine();
          System.out.print("Do you want to start the computer player? (y/n)");
          player = in.readLine();
          
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
        } catch (IOException e) {
          System.out.println(e.getMessage());
          System.out.println(USAGE);
        }
      }
      
      while (!started) {
        try {
          if (player.equals("y")) {
            System.out.print("Choose a maximum thinking time in seconds: ");
            double time = Double.parseDouble(in.readLine());
            System.out.println("Connecting to:" + adrs + ":" + port);
            client = new ClientHandler(socket, username, new ComputerPlayer(time));
            client.start();
            started = true;
          } else {
            System.out.println("Connecting to:" + adrs + ":" + port);
            client = new ClientHandler(socket, username, new HumanPlayer());
            client.start();
            started = true;
          }
        } catch (NumberFormatException e) {
          System.out.println("Input doesn't contain a number.");
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
  }

}

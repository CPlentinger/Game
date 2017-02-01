package project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public static final String USAGE = "Usage: <port>";
  public static ServerSocket sock;
  public static final String CAPABILITIES = Protocol.Server.SERVERCAPABILITIES + " 2 0 4 4 4 4 0";
  private static Socket socket1;
  private static Socket socket2;
  
  /**
   * Starts a server that accepts client connections at the port number specified by the user.
   * Tracks the amount of connections and gets the game capabilities.
   * Then a <code>ServerHandler</code> <code>Thread</code> will be started.
   * @param args , not used.
   */
  public static void main(String[] args) {
    int clients = 0;
    int port = 0;
    boolean started = false;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    while (started == false) {
      System.out.print("Please input the server port (between 0 and 65535): ");
      try {
        port = Integer.parseInt(in.readLine());
        sock = new ServerSocket(port);
        started = true;
      } catch (NumberFormatException exc) {
        System.out.println("Input is not a valid port number.");
        System.out.println(USAGE);
      } catch (BindException exc) {
        System.out.println("Port is already in use.");
      } catch (IOException exc) {
        System.out.println(exc.getMessage());
        System.exit(0);
      }
    }

    System.out.println("Started listening on port: " + sock.getLocalPort());
    while (!sock.isClosed()) {
      try {
        socket1 = new Socket();
        socket1 = sock.accept();
        System.out.println(sock.getInetAddress() 
            + " connected to the server, waiting for opponent...");
        clients++;
        socket2 = new Socket();
        socket2 = sock.accept();
        System.out.println(sock.getInetAddress() 
            + " connected to the server, handling capabilities.");
        clients++;
        CapabilitiesHandler caph1 = new CapabilitiesHandler(socket1, socket2, clients);
        String gameCapabilities = caph1.getGameCapabilities();
        
        new ServerHandler(socket1, socket2, gameCapabilities).start();
      } catch (IOException exc) {
        System.out.println(exc.getMessage());
        System.exit(0);
      }
    }
  }

}

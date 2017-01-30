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
  
  public static void main(String[] args) throws IOException {
    int clients = 0;
    int port = 0;
    boolean started = false;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    while (started == false) {
      System.out.print("Please input the server port (between 0 and 65535): ");
      try {
        port = Integer.valueOf(in.readLine());
        sock = new ServerSocket(port);
        started = true;
      } catch (NumberFormatException e) {
        System.out.println("Input is not a valid port number.");
      } catch (BindException e) {
        System.out.println("Port is already in use.");
      }
    }

    System.out.println("Started listening on port: " + sock.getLocalPort());
    while(!sock.isClosed()) {
      Socket socket1 = new Socket();
      socket1 = sock.accept();
      clients++;
      Socket socket2 = new Socket();
      socket2 = sock.accept();
      clients++;
      CapabilitiesHandler caph1 = new CapabilitiesHandler(socket1, socket2, clients);
      String gameCapabilities = caph1.getGameCapabilities();
      
      new ServerHandler(socket1, socket2, gameCapabilities).start();
    }
  }

}

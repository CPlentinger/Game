

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

  public static final String USAGE = "Usage: <port>";
  public static ServerSocket sock;
  
  public static void main(String[] args) throws IOException {
    int port = 0;
    boolean portSet = false;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    while (portSet == false) {
      System.out.print("Please input the server port (between 0 and 65535): ");
      try {
        port = Integer.valueOf(in.readLine());
        portSet = true;
      } catch (NumberFormatException e) {
        System.out.println("Input is not a valid port number.");
      }
    }
    
    sock = new ServerSocket(port);
    System.out.println("Started listening on port: " + sock.getLocalPort());
    while(!sock.isClosed()) {
      Socket socket1 = new Socket();;
      socket1 = sock.accept();
      
      Socket socket2 = new Socket();
      socket2 = sock.accept();
      new ServerHandler(socket1, socket2).start();
    }
  }

}

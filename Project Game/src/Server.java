

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

  public static final String USAGE = "Usage: <port>";
  public static ServerSocket sock;
  
  public static void main(String[] args) throws IOException {
    
    if (args.length != 1) {
      System.out.println(USAGE);
      System.exit(0);
    }
    
    int port = Integer.parseInt(args[0]);
    sock = new ServerSocket(port);
    
    while(true) {
      Socket socket1 = new Socket();
      socket1 = sock.accept();
      
      Socket socket2 = new Socket();
      socket2 = sock.accept();
      
      new ServerHandlerThread(socket1, socket2).start();
    }
  }

}

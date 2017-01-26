

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
  private static final String USAGE = "Usage: <address> <port>";
  
  public static void main(String[] args) throws IOException {
    
    if (args.length != 2) {
      System.out.println(USAGE);
      System.exit(0);
    }
    
    int port = Integer.parseInt(args[1]);
    InetAddress adrs = null;
    try {
      adrs = InetAddress.getByName(args[0]);
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 
    
    Socket socket = new Socket(adrs, port);
    new ClientHandler(socket).start();
  }

}

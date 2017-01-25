package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

  private Socket server;
  private BufferedReader in;
  private BufferedWriter out;
  
  public ClientHandler(Socket sock) throws IOException {
    this.server = sock;
    this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
  }
  
  public void run() {
    String message;
    try {
      while ((message = in.readLine()) != null) {
          System.out.println(message);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void handleTerminalInput() {
    String message = readString();
    while (message != null) {
      try {
        out.write(message);
        out.newLine();
        out.flush();
        message = readString();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public String readString() {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String message;
    try {
      return message = in.readLine();
    } catch (IOException e) {
      return null;
    }
  }
}

package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerHandlerThread extends Thread {

  private Socket client1;
  private Socket client2;
  private BufferedReader c1in;
  private BufferedWriter c1out;
  private BufferedReader c2in;
  private BufferedWriter c2out;
  private static int[] capabilities;
  public ServerHandlerThread(Socket sock1, Socket sock2) throws IOException {
    this.client1 = sock1;
    this.client2 = sock2;
    this.c1in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
    this.c1out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
    this.c2in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
    this.c2out = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
    this.capabilities = new int[8];
  }
  
  public void run() {
    sendToClient1(Protocol.Server.SERVERCAPABILITIES);
    readInput();
  }
  
  public void readInput() {
    String clientInput;
    try {
      while ((clientInput = c1in.readLine()) != null) {
        handleInput(clientInput);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void handleInput(String input) throws IOException {
      Scanner inScanner = new Scanner(input);
      if (inScanner.next()!="sendCapabilites") {
        sendToClient1("Please send your capabilites");
      }
      switch (inScanner.next()) {
        case "sendCapabilities": handleCapabilities(input);
                                 break;
        case "makeMove":         System.out.println("makeMove");
                                 break;
      }
      inScanner.close();
  }
  
  public void handleCapabilities(String capabilities) {
    Scanner capScanner = new Scanner(capabilities);
    capScanner.skip("sendCapabilities");
    int amountOfPlayers = capScanner.nextInt();
    String playerName = capScanner.next();
    int roomSupport = capScanner.nextInt();
    int maxRoomDimensionX = capScanner.nextInt();
    int maxRoomDimensionY = capScanner.nextInt();
    int maxRoomDimensionZ = capScanner.nextInt();
    int maxLengthToWin = capScanner.nextInt();
    int chatSupport = capScanner.nextInt();
    int autoRefresh = capScanner.nextInt();
  }
  public void sendToClient2(String message) {
    try {
      c2out.write(message);
      c2out.newLine();
      c2out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void sendToClient1(String message) {
    try {
      c1out.write(message);
      c1out.newLine();
      c1out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

package project.server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public class CapabilitiesHandler {
  private Socket client1;
  private Socket client2;
  private int c1ID;
  private int c2ID;
  private BufferedReader c1in;
  private BufferedWriter c1out;
  private BufferedReader c2in;
  private BufferedWriter c2out;
  private List<String> clientCapabilities;
  
/**
 * Creates a new <code>CapabilitieHandler<code> with the following references:
 * Sockets of both clients, buffered readers/writers on the input/output streams of the clients,
 * client IDs based on connection count of the <code>Server</code> and a list for the capabilities of both clients.
 * @param c1, socket of the first <code>Client</code> of the pair.
 * @param c2, socket of the second <code>Client</code> of the pair.
 * @param clients, integer representing the total connections with the <code>Server</code>.
 */
  public CapabilitiesHandler(Socket c1, Socket c2, int clients) {
    this.client1 = c1;
    this.client2 = c2;
    try {
      this.c1in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
      this.c1out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
      this.c2in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
      this.c2out = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.c1ID = clients - 1;
    this.c2ID = clients;
    this.clientCapabilities = new ArrayList<String>();
  }
  
  /**
   * Runs multiple methods to finally return the game capabilities.
   * @return game capabilities, minimum of all parties.
   */
  public String getGameCapabilities() {
    sendServerCapabilities();
    getClientCapabilities();
    assignIDs();
    return makeGameCapabilities();
  }

  /**
   * Sends the capabilities of the server to both clients.
   */
  private void sendServerCapabilities() {
    try {
      c1out.write(Server.CAPABILITIES);
      c1out.newLine();
      c1out.flush();
      System.out.println("out player " + c1ID + ": " + Server.CAPABILITIES);
      c2out.write(Server.CAPABILITIES);
      c2out.newLine();
      c2out.flush();
      System.out.println("out player " + c2ID + ": " + Server.CAPABILITIES);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * receives the client capabilities and stores them in the capability list.
   */
  private void getClientCapabilities() {
    try {
      clientCapabilities.add(c1in.readLine());
      System.out.println("in player " + c1ID + ": " + clientCapabilities.get(0));
      clientCapabilities.add(c2in.readLine());
      System.out.println("in player " + c2ID + ": " + clientCapabilities.get(0));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Sends an assign ID message to both clients containing their ID.
   */
  private void assignIDs() {
    try {
      c1out.write(Protocol.Server.ASSIGNID + " " + c1ID);
      c1out.newLine();
      c1out.flush();
      System.out.println("out player " + c1ID + ": " + Protocol.Server.ASSIGNID + " " + c1ID);
      c2out.write(Protocol.Server.ASSIGNID + " " + c2ID);
      c2out.newLine();
      c2out.flush();
      System.out.println("out player " + c2ID + ": " + Protocol.Server.ASSIGNID + " " + c2ID);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Scans both capability strings out of the list,
   * stores all the properties it contains and uses the important ones to create game capabilities.
   * Some properties aren't used because our game doesn't support it.
   * @return string containing game capabilities.
   */
  private String makeGameCapabilities() {
    Scanner c1 = new Scanner(clientCapabilities.get(0));
    Scanner c2 = new Scanner(clientCapabilities.get(1));
    c1.skip("sendCapabilities");
    c2.skip("sendCapabilities");
    
    @SuppressWarnings("unused")
    int amountOfPlayers = Math.min(c1.nextInt(), c2.nextInt());
    String p1Name = c1.next();
    String p2Name = c2.next();
    @SuppressWarnings("unused")
    int roomSupport = Math.min(c1.nextInt(), c2.nextInt());
    int maxRoomDimensionX = Math.min(c1.nextInt(), c2.nextInt());
    int maxRoomDimensionY = Math.min(c1.nextInt(), c2.nextInt());
    int maxRoomDimensionZ = Math.min(c1.nextInt(), c2.nextInt());  
    int lengthToWin = Math.min(c1.nextInt(), c2.nextInt());
    @SuppressWarnings("unused")
    int chatSupport = Math.min(c1.nextInt(), c2.nextInt());
    @SuppressWarnings("unused")
    int autoRefresh = Math.min(c1.nextInt(), c2.nextInt());
    
    StringJoiner joiner = new StringJoiner("|");
    joiner.add(" " + maxRoomDimensionX);
    joiner.add(String.valueOf(maxRoomDimensionY));
    joiner.add(String.valueOf(maxRoomDimensionZ));
    joiner.add(lengthToWin + " " + c1ID);
    joiner.add(p1Name);
    joiner.add("0000ff " + c2ID);
    joiner.add(p2Name);
    joiner.add("ff0000");
    String gameCapabilities = joiner.toString();
    c1.close();
    c2.close();
    return Protocol.Server.STARTGAME + gameCapabilities;
  }

}

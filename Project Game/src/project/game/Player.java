package project.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public abstract class Player {
  
  private String name;
  private String opponentName;
  private int ID;
  private Mark mark;
  protected Controller controller;
  
  
  public Player() {
    this.controller = new Controller();
  }
  
  public Controller getController() {
    return controller;
  }
  
  public void setController(Controller c) {
    this.controller = c;
  }
  
  public void setName(String pName) {
    this.name = pName;
  }
  
  public void setOpponentName(String oppName) {
    this.opponentName = oppName;
  }
  
  public void setID(int id) {
    this.ID = id;
  }
  
  public void setMark(Mark m) {
    this.mark = m;
  }
  
  public String getName() {
    return name;
  }
  
  public String getOpponentName() {
    return opponentName;
  }
  
  public int getID() {
    return ID;
  }
  
  public Mark getMark() {
    return mark;
  }
  
  public void buildBoard(String startMessage) {
    controller.buildBoard(startMessage);
  }
  
  public void makeMove(int x, int y, Mark m) {
    controller.makeMove(x, y, m);
  }
  
  public boolean checkMove(int x, int y) {
    return controller.checkMove(x, y);
  }
  
  public String endGameCheck(int id) {
    return controller.endGameCheck(id);
  }
  
  public String getInput(String question) {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String result = null;
    try {
      result = in.readLine();
    } catch (IOException e) {
      
    }
    if (result != null) {
      return result;
    } else {
      return "";
    }
  }
  
  public abstract String getMove(String question);

}

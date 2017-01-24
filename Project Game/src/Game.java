
public class Game {
  public Controller controller;
  public TUIView view;
  
  public Game(Controller c) {
    this.controller = c;
    this.view = new TUIView(c);
    controller.board.addObserver(view);
  }
  
  public void start() {
    
  }
  
  
  
}

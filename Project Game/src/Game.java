
public class Game {

  public static void main(String[] args) {
    TUIView view = new TUIView(new Board(4));
    view.start();
  }
}

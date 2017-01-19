package Project;

public class Board {
	public Layer[] layers;
	public Board(int bdim, int height) {
		layers = new Layer[height];
		for (int i = 0; i<height; i++) {
			layers[i] = new Layer(bdim, i); 
		}
	}
	
	public static void main(String[] args) {
		Board one = new Board(4, 5);
	}
	
	
}
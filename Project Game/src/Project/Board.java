package Project;

public class Board {
	public static final int DIM = 4;
	public static final String[] NUMBERING = {" 00 | 01 | 02 | 03 ", "---+---+---+---", " 04 | 05 | 06 | 07 ", 
			"---+---+---+---", " 08 | 09 | 10 | 11 ", "---+---+---+---", " 12 | 13 | 14 | 15 " };
	public static final String LINE = NUMBERING[1];
	public static final String DELIM = "     ";
	private Mark[] fields;
	
	public Board() {
		this.fields = new Mark[DIM*DIM];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = Mark.E;
		}
	}
	
	public int index(int row, int col) {
		return row*DIM + col;
	}
	
	public boolean isField(int index) {
		return 0 <= index && index < DIM*DIM;
	}
	
	public boolean isField(int row, int col) {
		return isField(index(row, col));
	}
	
	public Mark getField(int i) {
		if (isField(i)) {
			return fields[i];
		} else {
			return null;
		}
	}
	
	public Mark getField(int row, int col) {
		if (isField(row, col)) {
			return fields[index(row,col)];
		} else {
			return null;
		}
	}
	
//	public String toString() {
//		String s = "";
//		
//		for (int i = 0; i < DIM; i++) {
//			String row = "";
//			for (int j = 0; j < DIM; j++) {
//				
//				row = row + " " + getField(i, j).toString() + " ";
//				if (j < DIM -1) {
//					row = row + "|";
//				}
//			}
//			s = s + row + DELIM + NUMBERING[i*2];
//			if (i < DIM-1) {
//				s = s + LINE + "\n";
//			}
//			}
//		return s;
//	}
//	
	public static void main(String[] args) {
		Board one = new Board();
		System.out.println(one.toString());
	}
}
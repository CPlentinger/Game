package Project;

public class Layer {
	public int DIM;
	public int laynum;
	public Mark[] fields;
	public String[] NUMBERING;
	public static void main(String[] args) {
		Layer one = new Layer(4, 0);
		one.setField(0, Mark.B);
		one.setField(5, Mark.B);
		one.setField(10, Mark.B);
		one.setField(15, Mark.B);
		System.out.println(one.hasDiagonal(Mark.B));
	}
	
	public Layer(int BDIM, int lnum) {
		this.DIM = BDIM;
		this.laynum = lnum;
		this.fields = new Mark[DIM*DIM];
		for (int i = 0; i < DIM*DIM; i++) {
			this.fields[i] = Mark.E;
		}
		this.NUMBERING = new String[DIM*DIM];
		for (int j = 0; j < DIM; j++) {
			NUMBERING[j] = " " + String.format("%02d", (DIM*j+(laynum*DIM*DIM))) + " ";
			for (int k = 1; k < DIM; k++) {
				NUMBERING[j] += "| " + String.format("%02d", k+(j*DIM)+(laynum*DIM*DIM)) + " ";
			}
		System.out.println(this.NUMBERING[j]);
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
	
	public void setField(int i, Mark m) {
    	if (isField(i)) {
        	fields[i] = m;
    	}
    }
	
    public void setField(int row, int col, Mark m) {
    	if (isField(row,col)) {
    		fields[index(row,col)] = m;
    	}
    }
	public boolean isEmptyField(int i) {
    	if (isField(i)) {
    		return getField(i).equals(Mark.E);
    	} else {
    		return false;
    	}
    }
	
	public boolean isEmptyField(int row, int col) {
    	if (isField(row,col)) {
    		return getField(row,col).equals(Mark.E);
    	} else {
    		return false;
    	}
    }
	
	public boolean hasDiagonal(Mark m){
		int diag1 = 0;
    	int diag2 = 0;
    	for (int i = 0; i <= DIM*DIM; i++) {
    		if (getField(i).equals(m)) {
    			diag1++;
    		}
    	}
    	for (int j = DIM-1; j <= (DIM*DIM)-DIM; j += DIM-1) {
    		if (getField(j).equals(m)) {
    			diag2++;
    		}
    	}
    	return diag1 == DIM || diag2 == DIM;
	}
	
	public boolean isFull() {
    	int filled = 0;
    	for (int i = 0; i < DIM*DIM; i++) {
    		if (!isEmptyField(i)) {
    			filled++;
    		}
    	}
        return filled == DIM*DIM;
    }
	
    public void reset() {
    	for (int i = 0; i < DIM*DIM; i++) {
    		this.setField(i, Mark.E);
    	}
    }
}

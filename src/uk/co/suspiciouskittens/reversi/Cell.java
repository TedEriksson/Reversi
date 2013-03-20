package uk.co.suspiciouskittens.reversi;

public class Cell {
	private int state = 0;
	private int position;
	private boolean hinting = false;
	
	public int getPosition() {
		return position;
	}

	public boolean isHinting() {
		return hinting;
	}

	public void setHinting(boolean hinting) {
		this.hinting = hinting;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Cell() {
		
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
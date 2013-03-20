package uk.co.suspiciouskittens.reversi;

import java.util.ArrayList;

public class Queue {
	ArrayList<Integer> queue = new ArrayList<Integer>();
	
	public void add(int position) {
		queue.add(position);
	}
	
	public int size() {
		return queue.size();
	}
	
	public int get(int index) {
		return queue.get(index);
	}
	
	public int remove() {
		return queue.remove(0);
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}

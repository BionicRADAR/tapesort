package sorts.tapesort;

import java.util.ArrayList;

/**
 * A class to simulate a tape-style datastructure for testing tapesort algorithms
 * @author Nathaniel Schleicher
 *
 * @param <T> The class of the objects to be stored on the tape (e.g., Integer)
 */
public class Tape<T extends Comparable<T>>{
	
	/*
	 * Keeps track of how many times a tape has been written to;
	 * Used because this project seeks to compare performance of tapesort algorithms
	 * by number of writes.
	 */
	private int writes = 0;
	
	/*
	 * Makes a new empty tape.
	 */
	public Tape() {
		this.head = new Node<T>();
		this.current = this.head;
	}
	
	/*
	 * Makes a new tape with head Node containing the passed object
	 */
	public Tape(T head) {
		this.head = new Node<T>(head);
		this.current = this.head;
	}
	
	/*
	 * Makes a tape from an input int[]
	 * The tape has the same elements in the same order as the int[]
	 */
	public Tape<Integer> intTape (Integer[] intArray) {
		Tape<Integer> toReturn = new Tape<Integer>();
		for (int i = 0; i < intArray.length; i++) {
			toReturn.write(intArray[i]);
			toReturn.advance();
		}
		toReturn.rewind();
		return toReturn;
	}
	
	/*
	 * Makes a tape from an input int[], keeping the elements in order,
	 * but instead of storing the ints in Integer objects, stores them in
	 * CompCounter objects (see CompCounter class)
	 */
	public Tape<CompCounter> compTape (int[] intArray) {
		Tape<CompCounter> toReturn = new Tape<CompCounter>();
		for (int i = 0; i < intArray.length; i++) {
			toReturn.write(new CompCounter(intArray[i]));
			toReturn.advance();
		}
		toReturn.rewind();
		toReturn.resetWrites();
		return toReturn;
	}
	
	/**
	 * Resets the current position to the beginning ("head") of the tape
	 */
	public void rewind() {
		current = head;
	}
	
	/**
	 * Erases/empties the entire tape; resets the current position to the head
	 */
	public void erase() {
		this.head = new Node<T>();
		this.current = this.head;
	}
	
	/**
	 * Reads the current tape position and returns the object stored there
	 * @return The object stored at the current position on the tape
	 */
	public T read() {
		return current.datum;
	}
	
	/**
	 * Advances the current position on the tape unless the current position has no datum.
	 */
	public void advance() {
		if (current.datum == null) //Does not advance if current position has no datum
			return;
		if (current.next == null) { //Makes new node if needed to advance to
			current.next = new Node<T>();
		}
		current = current.next;
	}
	
	/**
	 * Writes the datum to the current tape position and increments the writes counter
	 * @param datum The object to be written to the tape
	 */
	public void write(T datum) {
		writes++;
		current.datum = datum;
	}
	
	/**
	 * Prints the contents of the tape in order, separated by spaces.
	 * Output relies on toString methods of the objects on the tape
	 */
	public void print() {
		for (Node<T> now = head; now.datum != null; now = now.next) {
			System.out.print(now.datum + " ");
			if (now.next == null)
				break;
		}
		System.out.println();

	}
	
	/**
	 * Creates an ArrayList with the contents of the tape 
	 * Side effect: current position will be set to the end of the tape.
	 * @return An ArrayList containing the tape's contents
	 */
	public ArrayList<T> toArrayList() {
		ArrayList<T> toReturn = new ArrayList<T>();
		for (rewind(); read() != null; advance()) {
			toReturn.add(read());
		}
		return toReturn;
	}
	
	/**
	 * Returns the number of writes to this tape so far
	 * @return The number of times this tape has been written to.
	 */
	public int getWrites() {
		return writes;
	}
	
	/**
	 * Resets the number of writes to this tape to zero; should be done before
	 * making measurements/testing the tapesort algorithm's efficiency.
	 */
	public void resetWrites() {
		writes = 0;
	}
	
	/**
	 * The first node of the tape
	 */
	public Node<T> head;
	
	/**
	 * The current node of the tape
	 */
	public Node<T> current;
	
}
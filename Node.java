package sorts.tapesort;

/**
 * Part of the tape datastructure; represents an individual position on a tape
 * @author Nathaniel Schleicher
 *
 * @param <T> The class of the objects stored on the tape
 */
public class Node<T extends Comparable<T>> {
	
	/**
	 * Makes a new empty Node
	 */
	public Node() {
		this.datum = null;
		this.next = null;
	}
	
	/**
	 * Makes a new node with the given datum
	 * @param datum The object to be stored on the Node
	 */
	public Node(T datum) {
		this.datum = datum;
		this.next = null;
	}
	
	/**
	 * The object on the Node
	 */
	public T datum;
	
	/**
	 * The next Node on the tape
	 */
	public Node<T> next;
	
}
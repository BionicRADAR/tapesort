package sorts.tapesort;

/**
 * This class exists entirely to test efficiency of various tape sort algorithms.
 * It is essentially an Integer whose compareTo() also keeps track of a static variable,
 * recording how many times CompCounters have been compared. It also has a reset() method
 * to reset the number of comparisons to 0 so that a new test can be done.
 * 
 * @author Nathaniel Schleicher
 *
 */
public class CompCounter implements Comparable<CompCounter> {
	
	/**
	 * The entire point of this class; comparisons keeps track of how many times a CompCounter's compareTo()
	 * has been called since the last time it was reset()
	 */
	private static int comparisons = 0;
	
	/**
	 * int Constructor; value is initialized to a new Integer whose value is val.
	 * @param val The int to be wrapped in a CompCounter
	 */
	public CompCounter(int val) {
		value = new Integer(val);
	}
	
	/**
	 * Integer Constructor value is initialized to a new Integer whose value is val's.
	 * @param val The Integer to be wrapped in a CompCounter
	 */
	public CompCounter(Integer val) {
		value = new Integer(val);
	}
	
	/**
	 * CompCounter is an Integer wrapper; value handles nearly everything CompCounter needs to do except tracking
	 */
	private Integer value;

	/**
	 * Just like the Integer compareTo(), except it takes only CompCounters as arguments and increments comparisons,
	 * to show that another comparison has been done, which is precisely why this class exists: to track the number
	 * of comparisons done.
	 * @return Whether this CompCounter's value is less than (< 0), equal to (0) or greater than (> 0) the argument CompCounter's value, as the Integer compareTo() would do
	 */
	@Override
	public int compareTo(CompCounter c) {
		comparisons++;
		return value.compareTo(c.getValue());
	}
	
	/**
	 * Get the Integer that CompCounter is wrapped around.
	 * @return The CompCounter's main datum, the Integer value
	 */
	public Integer getValue(){
		return value;
	}
	
	/**
	 * Reset the number of comparisons counted in preparation for a new test.
	 */
	public static void resetComparisons() {
		comparisons = 0;
	}
	
	/**
	 * Get the number of times compareTo() has been called, in order to track how many comparisons have been done.
	 * @return The number of times compareTo() has been called since the last reset()
	 */
	public static int getComparisons() {
		return comparisons;
	}
	
	/**
	 * Basically gives the Integer's toString(), which should basically be an int. Useful for printing and checking
	 * for correctness.
	 * @return A string representing the integral value stored in this CompCounter
	 */
	@Override
	public String toString() {
		return value + "";
	}
}

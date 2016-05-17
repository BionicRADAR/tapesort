package sorts.tapesort;
import java.util.*;

/**
 * The actual sorting algorithms for the various tapesorts are in this class,
 * namely the standard 3-tape tapesort (sort()), tapesort with variable tape number (multiSort()),
 * and a balanced tapesort (balancedSort())
 * These tape sorts are essentially merge sorts for use on data tapes, which have sequential access.
 * The sorts use the compareTo() method to be compatible with various objects; compareTo() requires
 * that the Comparable interface be implemented by the objects stored on the tapes. The sorts sort in
 * ascending order, which is to say for any i1, i2 on the sorted tape, i1 coming before i2, 
 * i1.compareTo(i2) <= 0. 
 * The tapes used here are objects whose type is defined in the Tape class, which merely simulate
 * tapes. These tapes can only be advanced one way, unless they are rewound to the beginning, and
 * they will only advance when advance() is called if what is stored in the current position is not null; the 
 * algorithms take advantage of this. An advance method could be written for actual tapes which also does this, but
 * if the read time on a tape is long, such a method could be sub-optimal, and these algorithms would
 * need to be somewhat rewritten for efficiency.
 * These algorithms were written to compare efficiency in number of comparisons and writes done using various
 * sorting algorithms on random data. A few parts of this class exist solely for tracking purposes; fortunately,
 * most of them are stuck in the write() method of the Tape class and the compareTo() method in the object
 * I use to sort, the CompCounter.
 * @author Nathaniel Schleicher
 *
 * @param <T> The class of the objects stored on the tapes. Must implement Comparable for use of compareTo().
 */
public class TapeSorter<T extends Comparable<T>> {
	
	/**
	 * This is the complete list of tapes used to sort the data. It only exists to help track the number
	 * of writes done while sorting; it can be omitted if tracking is not required.
	 */
	private ArrayList<Tape<T>> fullTapeList = new ArrayList<Tape<T>>();
	
	/**
	 * The standard 3-tape sort. It splits the data out form the input tape to two other tapes,
	 * then merges back from those tapes to the first one, and repeats until the data is sorted.
	 * @param toSort The tape containing the data to be sorted. It is altered during the method's running.
	 * @return A tape with the data sorted on it. The returned tape is actually toSort, with the data on it sorted.
	 */
	public Tape<T> sort(Tape<T> toSort) {
		toSort.rewind(); //Rewind toSort in preparation for sorting, in case it is not rewound
		fullTapeList.add(toSort); //This is only done for tracking purposes (ie, to track number of writes)
		ArrayList<Tape<T>> tapes = new ArrayList<Tape<T>>();
		tapes.add(new Tape<T>());
		fullTapeList.add(tapes.get(0)); //This is only done for tracking purposes
		tapes.add(new Tape<T>()); //tapes now has 2 tapes in it, which with toSort makes the 3 tapes necessary for the sort
		fullTapeList.add(tapes.get(1)); //This is only done for tracking purposes
		//The tapes will be split and merged repeatedly until they are sorted, beginning here
		//There is no defined end condition here because, due to optimizations in the algorithm, there is no definite
		//number of iterations it will make. There is an upper limit, but the algorithm already checks if toSort
		//is sorted during its normal execution, often resulting in far fewer steps in this loop.
		//Having this upper limit in the loop conditions might speed up execution in the worst case, but tapes
		//don't provide a definite length, which would be required to know the upper limit, and counting the length
		//of the tape would potentially take almost as much time as what is currently done additionally in the worst case.
		//This would not be true if advancing forward through a tape took a long time, in which case during an early
		//read of toSort the length should be determined and used as an upper limit on loops for efficiency.
		//Notably, though, in testing, the algorithm rarely makes the upper limit of loops, most frequently stopping
		//one loop short, even when the tape is of a size where the maximum number of loops happening based on its size
		//is most likely.
		for(;;){
			int currentTape = 0;
			tapes.get(currentTape).write(toSort.read()); //Prime the tape by writing the first element on it.
			//The first element will be written on this tape regardless, and writing it now allows compareTo() to be
			//called in the while loop below immediately.
			
			toSort.advance();
			//Split toSort onto the two tapes once
			//This is done by splitting the items in toSort into groups of ascending order;
			//Whenever the an item in toSort descends from the previous one, it must start a new division
			//This creates uniformly ascending divisions, as is required by mergesort, and minimizes
			//how many of them there are, to optimize its speed.
			while (toSort.read() != null) {//repeat until we've made it through all of toSort
				//Condition 1: start a new division to be merged later.
				//It does this when the next item does not come after the last one on the current tape.
				if (toSort.read().compareTo(tapes.get(currentTape).read()) < 0) {
					currentTape = (currentTape + 1) % 2;
					tapes.get(currentTape).advance();
					tapes.get(currentTape).write(toSort.read());
				}
				//Condition 2: continue writing to the current tape.
				//It does this when the next item comes after the current one.
				else {
					tapes.get(currentTape).advance();
					tapes.get(currentTape).write(toSort.read());
				}
				toSort.advance();
			}
			for (Tape<T> tape: tapes) {//Rewind all tapes in preparation for the next step
				tape.rewind();
			}
			//End case: the entirety of toSort was written to tape0 (tape1 is empty), meaning toSort
			//was entirely ordered. We rewind toSort and return it.
			if (tapes.get(1).read() == null) {
				toSort.rewind();
				return toSort;
			}
			toSort.erase(); //Erase toSort in preparation for merging to it.
			//Merging begins below; will merge until out of data on one of the tapes.
			while(tapes.get(0).read() != null && tapes.get(1).read() != null) {
				//Write first item onto toSort; it is the least of the first items on the other tapes.
				if (toSort.read() == null)
					if (tapes.get(0).read().compareTo(tapes.get(1).read()) < 0) {
						toSort.write(tapes.get(0).read());
						tapes.get(0).advance();
					}
					else {
						toSort.write(tapes.get(1).read());
						tapes.get(1).advance();
					}
				//If the division on tape0 has ended (its next item is less than the last item on toSort)
				//empty items from tape1 until its next item is less than the last item on toSort
				//(End the current division on tape1). 
				//This is like having emptied one sub-array in a normal merge sort.
				else if (tapes.get(0).read().compareTo(toSort.read()) < 0) {
					while(tapes.get(1).read() != null && tapes.get(1).read().compareTo(toSort.read()) >= 0) {
						toSort.advance();
						toSort.write(tapes.get(1).read());
						tapes.get(1).advance();
					}
					toSort.advance();
				}
				//Same as above, but with the tapes' roles switched
				else if (tapes.get(1).read().compareTo(toSort.read()) < 0) {
					while(tapes.get(0).read() != null && tapes.get(0).read().compareTo(toSort.read()) >= 0) {
						toSort.advance();
						toSort.write(tapes.get(0).read());
						tapes.get(0).advance();
					}
					toSort.advance();
				}
				//If both tapes are within their "current" divisions, write the next item down, the least of
				//the two tapes' current items. This is the basic merge step.
				else if (tapes.get(0).read().compareTo(tapes.get(1).read()) < 0) {//tape0's item is less
					toSort.advance();
					toSort.write(tapes.get(0).read());
					tapes.get(0).advance();
				}
				else {//tape1's item is less
					toSort.advance();
					toSort.write(tapes.get(1).read());
					tapes.get(1).advance();
				}
			}
			//Once one tape is completely empty, empty the rest of the other tape onto toSort to finish this merge step
			for(Tape<T> tape : tapes) {
				for(; tape.read() != null; tape.advance()) {
					toSort.advance();
					toSort.write(tape.read());
				}
				tape.erase();
			}
			toSort.rewind();
		}
	}
	
	/**
	 * A more complex tape sort algorithm, using three or more tapes. Using three tapes
	 * will duplicate the effects of the above algorithm. The number of tapes actually used
	 * will be numTapes + 1, the additional tape being toSort
	 * @param toSort the tape containing the data to be sorted. toSort will be modified.
	 * @param numTapes the number of additional tapes to be used to sort the data.
	 * @throws Exception an exception thrown if not enough tapes are provided to sort the data; numTapes must be at least 2
	 * @return a tape with the data sorted on it; is actually the input tape, toSort
	 */
	public Tape<T> multiSort(Tape<T> toSort, int numTapes) throws Exception {
		if (numTapes < 2) //Need at least 3 tapes total to sort
			throw new Exception("Not enough tapes");
		toSort.rewind(); //Rewind toSort in preparation
		fullTapeList.add(toSort); //Tracking purposes
		ArrayList<Tape<T>> tapes = new ArrayList<Tape<T>>(); //List of other tapes used
		for (int i = 0; i < numTapes; i++) { //Filling tapes
			tapes.add(new Tape<T>());
			fullTapeList.add(tapes.get(i)); //Tracking purposes only
		}
		for(;;) {//Continue until sorted; will break then. See explanation in "sort" above
			int currentTape = 0;
			tapes.get(currentTape).write(toSort.read()); //Put first item on first tape in preparation for while loop
			toSort.advance();
			//Below starts the "split" step
			while (toSort.read() != null) { //Continue splitting until we've made it completely through toSort
				//Condition one: if the next item does not follow from the one currently on the tape, switch to the
				//next tape
				if (toSort.read().compareTo(tapes.get(currentTape).read()) < 0) {
					currentTape = (currentTape + 1) % numTapes;
				}
				//Now write to whichever is the current tape
				tapes.get(currentTape).advance(); //Takes advantage of the quirks of the advance() method
				tapes.get(currentTape).write(toSort.read());

				toSort.advance();//Advance toSort to the next item to be written
			}
			for (Tape<T> tape: tapes) {//Rewind split tapes to prepare for merging
				tape.rewind();
			}
			//End Case: toSort was sorted, so it was all written to tape 0, so tape 1 (and all tapes after it)
			//is empty.
			if (tapes.get(1).read() == null) {
				toSort.rewind(); //Rewind before returning
				return toSort;   //Return sorted tape
			}
			toSort.erase(); //Erase toSort in preparation for merging to it.
			
			//This array tells which tapes still have items on them to be merged.
			//A tape is no longer active once either a "section" has ended (its next value is less than the
			//previous one) or its next item is null.
			int[] activeTapes = new int[tapes.size()];
			int nullCount = 0; //The number of empty tapes
			for (int i = 0; i < activeTapes.length; i++) //give activeTapes and nullCount their start values
				if (tapes.get(i).read() == null) {//It is an empty tape, so it is not active
					nullCount++;
					activeTapes[i] = 0;
				}
				else //The tape is not empty, so it is active
					activeTapes[i] = 1;
			//Merge step; repeat until tapes have been merged back to toSort
			for(;;) {
				//minTape is the position of the minimum tape in tapes that has the smallest item that is larger
				//than the last item on toSort (i.e. the tape that has the next value to be merged)
				int minTape = 0;
				int i = 0;
				for (; i < tapes.size(); i++)//Find the first active tape
					if (activeTapes[i] == 1)
						break;
				minTape = i; //minTape is now the position of the first active tape
				
				if (minTape == tapes.size()) { //There is no active tape
					//Make all non-empty tapes active to prepare for next step of merging
					for (i = 0; i < tapes.size(); i++) {
						if (tapes.get(i).read() != null) {
							activeTapes[i] = 1;
						}
					}
					continue;
				}
				
				//Find the actual minTape
				for (; i < activeTapes.length; i++)
					//Conditions: the tape must be active, and the item on the tape must be less than the one
					//on the current minTape
					if (activeTapes[i] == 1 && tapes.get(i).read().compareTo(tapes.get(minTape).read()) < 0) {
						minTape = i;
					}
				//Write the next item to toSort; advance the tape that just wrote
				toSort.advance();
				toSort.write(tapes.get(minTape).read());
				tapes.get(minTape).advance();
				
				//Check to see if we must update the status of the tape that just wrote
				if (tapes.get(minTape).read() == null) {//The tape is empty
					nullCount++; //We have one more empty tape
					activeTapes[minTape] = 0; //It is no longer active
					if (nullCount == activeTapes.length) //End case for the merge step: all tapes are empty
						break;
				}
				
				//The next item on the tape is less than the one just written; then we have entered a new
				//merge section, which must wait until the next merge.
				else if (tapes.get(minTape).read().compareTo(toSort.read()) < 0) {
					activeTapes[minTape] = 0; //The tape is no longer active until we move to the next merge sections
				}
			}
			
			//Prepare for next splitting step
			for(Tape<T> tape : tapes) {
				tape.erase();
			}
			toSort.rewind();
		}
	}
	
	/**
	 * A more complicated tape sort which simultaneously splits and merges by having two sets of tapes,
	 * and swapping which ones are being merged to each step. This prevents needing to go through one tape
	 * repeatedly to split back out to the other tapes. The two sets of tapes are of the same size.
	 * @param toSort The tape containing the data to be sorted; is modified
	 * @param numTapes The number of tapes in each group of tapes; total number of tapes used will be 2 * numTapes. Must be at least 2
	 * @return A tape containing the sorted data; unlike sort and multiSort, this tape might not be toSort
	 */
	public Tape<T> balancedSort(Tape<T> toSort, int numTapes) {
		toSort.rewind(); //Prepare for sorting
		if (numTapes < 2) { //Need at least 2 sets of 2 tapes for a balanced tape sort
			System.err.println("Too few tapes");
			return toSort;
		}
		ArrayList<Tape<T>> from = new ArrayList<Tape<T>>(); //list of tapes to write "from"
		from.add(toSort); //toSort is the first tape in the from tapes, since it must be written from at the start
		for (int i = 1; i < numTapes; i++) //Fill the rest of from. It has numTapes tapes.
			from.add(new Tape<T>());
		ArrayList<Tape<T>> to = new ArrayList<Tape<T>>(); //list of tapes to write "to"
		for (int i = 0; i < numTapes; i++) //fill "to" with empty tapes; to has numTapes tapes.
			to.add(new Tape<T>());
		//At this point, there are 2 * numTapes tapes, half in from, and half in to. One of them is toSort.
		//from's tapes are mostly empty, except for toSort, which must start by being split from to "to".
		//to's tapes are all empty and ready to be split to.
		
		//Entirely for tracking purposes. Next four lines can be removed if tracking performance is unnecessary.
		for (Tape<T> tape : from)
			fullTapeList.add(tape);
		for (Tape<T> tape : to)
			fullTapeList.add(tape);
		
		//Which tape we are writing to. Any time we enter a new merge segment, this will be changed to the next to tape.
		int activeToTape = 0;
		
		//Much like in multiSort, we must keep track of which tapes are being sorted from.
		int activeFromTapes[] = new int[numTapes];
		
		//This is our empty tape count again. Since this is the first step, we know that only toSort is not empty,
		//so the rest of the from tapes count for the nullCount.
		int nullCount = numTapes - 1;
		activeFromTapes[0] = 1; //toSort is not empty
		for (int i = 1; i < numTapes; i++) //All the other from tapes are empty.
			activeFromTapes[i] = 0;
		
		//Continue to merge/split until the data is sorted.
		for (;;) {
			int i = 0; //Declared here so iteration can be resumed partially through
			int minTape = 0;
			for (; i < numTapes; i++) { //Find the first active from tape
				if (activeFromTapes[i] == 1)
					break;
			}
			minTape = i; //First valid tape stored here before we find the actual min
			if (minTape == numTapes) { //There are no more active from tapes; we must move to the next set of merges
				//The tape we are writing to changes. This is the key part of the balanced tapeSort, as it allows us
				//to simultaneously split and merge; whenever we start a new section of merges, we simply change which
				//tape we are merging to. In the end, all "to" tapes will have series of ascending data.
				activeToTape = (activeToTape + 1) % numTapes; //Select the next "to" tape
				for (int j = 0; j < numTapes; j++) { //Check which tapes are not empty; make them active
					if (from.get(j).read() != null)
						activeFromTapes[j] = 1;
				}
				continue;
			}
			for (; i < numTapes; i++) { //Find the actual minTape
				if (activeFromTapes[i] == 1 && from.get(i).read().compareTo(from.get(minTape).read()) < 1) 
					minTape = i;
			}
			//Write the min value to the active "to" tape
			to.get(activeToTape).advance();
			to.get(activeToTape).write(from.get(minTape).read());
			from.get(minTape).advance();
			//Check to see if the status of the "from" tape that was just written from must change.
			if (from.get(minTape).read() == null) { //The from tape is empty
				activeFromTapes[minTape] = 0; //It is no longer active
				nullCount++; //There is one more empty tape
				if (nullCount == numTapes) {//All "from" tapes have been emptied, so we must move to the next step
					
					//End case: all values have been merged to one tape (and are thus completely sorted),
					//which means that a rewound tape 1 will be empty.
					to.get(1).rewind();
					if (to.get(1).read() == null)
						break;
					
					//Swap the from and to tapes. This is another key to the balanced tape sort.
					//Now the tapes with all the data on them must merge/split to the tapes without data.
					ArrayList<Tape<T>> temp = from;
					from = to;
					to = temp;
					
					nullCount = 0; //Reset the null count
					for (int j = 0; j < numTapes; j++) {
						from.get(j).rewind(); //Rewind all our new from tapes in preparation for the next merge/splits
						if (from.get(j).read() == null) { //If nearly done sorting, some of the from tapes might be empty
							nullCount++; //nullCount must reflect the existence of an empty from tape
							activeFromTapes[j] = 0; //that tape is not active
						}
						else
							activeFromTapes[j] = 1; //a non-empty tape is active
					}
					for (Tape<T> tape : to) //Erase all the new "to" tapes in preparation for merging to them
						tape.erase();
					//The first tape to be written to is "to" tape 0. This is partially because of how the algorithm
					//detects that the data is sorted
					activeToTape = 0; 
				}
			}
			//The tape's next value does not follow from the one just written; it has entered a new merge section
			else if (from.get(minTape).read().compareTo(to.get(activeToTape).read()) < 0) {
				activeFromTapes[minTape] = 0; //It is not empty, but it is no longer active
			}
		}
		//The "end case" break just happened, so these are the last steps.
		to.get(0).rewind(); //Rewind "to" tape zero in preparation of returning it
		return to.get(0); //Return the sorted tape
	}
	
	/**
	 * This method exists entirely for efficiency tracking purposes. All tapes (as written) track the number
	 * of writes to them, and all tapes used are stored in a private list of tapes (fullTapeList) entirely
	 * so that their number of writes can be returned here. If one TapeSorter sorts multiple tapes in a row,
	 * its count will be the sum of all writes used in all its sorts. To start back at 0, a new TapeSorter object must
	 * be initialized and used.
	 * @return The number of total writes done by this TapeSorter object.
	 */
	public int getTotalWrites() {
		int totalWrites = 0;
		for (Tape<T> tape : fullTapeList)
			totalWrites += tape.getWrites();
		return totalWrites;
	}
}

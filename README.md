# tapesort
A Java tapesort implementation. Written when I was partway through college and for an assignment, so it is fairly messy, but it works. Considering the reputation tapesort had at my college (for being difficult), I was happy to find it fairly easy and wrote multiple varieties of tapesorts.

A tapesort is a sorting algorithm specifically for datatapes. Generally, the tapes are considered to advance forward one step at a time and rewind all at once; it's possible that a tape could have linear access time in either direction, but this would still limit the sorts used. Tapesorts are essentially mergesorts with a little bit of extra cleverness to handle the linear access style of the tapes.

Included are a basic 3-tape sort, a sort for an arbitrary number of tapes, and a balanced tapesort for any even number of tapes. The sorts use generics, allowing them to be used for any Comparable. The sorts are in TapeSorter.java. Also, there are classes to simulate a tape (Tape.java and Node.java), and another class for performance rating, the CompCounter (CompCounter.java)

This project was originally a part of a class project that included testing the performance of various sorts against each other, thus some code can be found in there for counting comparisons and number of writes.

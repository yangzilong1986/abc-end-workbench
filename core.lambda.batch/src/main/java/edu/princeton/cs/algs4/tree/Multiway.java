/******************************************************************************
 *  Compilation:  javac Multiway.java
 *  Execution:    java Multiway input1.txt input2.txt input3.txt ...
 *  Dependencies: IndexMinPQ.java In.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/24pq/m1.txt
 *                http://algs4.cs.princeton.edu/24pq/m2.txt
 *                http://algs4.cs.princeton.edu/24pq/m3.txt
 * 
 *  Merges together the sorted input stream given as command-line arguments
 *  into a single sorted output stream on standard output.
 *
 *  % more m1.txt 
 *  A B C F G I I Z
 *
 *  % more m2.txt 
 *  B D H P Q Q
 * 
 *  % more m3.txt 
 *  A B E F J N
 *
 *  % java Multiway m1.txt m2.txt m3.txt 
 *  A A B B B C D E F F G H I I J N P Q Q Z 
 *
 ******************************************************************************/

package edu.princeton.cs.algs4.tree;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.tree.IndexMinPQ;
import edu.princeton.cs.algs4.utils.In;

/**
 *  The {@code Multiway} class provides a client for reading in several
 *  sorted text files and merging them together into a single sorted
 *  text stream.
 *  This implementation uses a {@link IndexMinPQ} to perform the multiway
 *  merge. 
 */

public class Multiway { 

    // This class should not be instantiated.
    private Multiway() { }

    // merge together the sorted input streams and write the sorted result to standard output
    private static void merge(In[] streams) {
        int n = streams.length;
        IndexMinPQ<String> pq = new IndexMinPQ<String>(n);
        for (int i = 0; i < n; i++)
            if (!streams[i].isEmpty())
                pq.insert(i, streams[i].readString());

        // Extract and print min and read next from its stream. 
        while (!pq.isEmpty()) {
            StdOut.print(pq.minKey() + " ");
            int i = pq.delMin();
            if (!streams[i].isEmpty())
                pq.insert(i, streams[i].readString());
        }
        StdOut.println();
    }


    /**
     *  Reads sorted text files specified as command-line arguments;
     *  merges them together into a sorted output; and writes
     *  the results to standard output.
     *  Note: this client does not check that the input files are sorted.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int n = args.length;
        In[] streams = new In[n];
        for (int i = 0; i < n; i++)
            streams[i] = new In(args[i]);
        merge(streams);
    }
}
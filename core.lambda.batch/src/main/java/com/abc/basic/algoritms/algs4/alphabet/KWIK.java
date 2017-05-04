package com.abc.basic.algoritms.algs4.alphabet;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdIn;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  The {@code KWIK} class provides a {@link SuffixArray} client for computing
 *  all occurrences of a keyword in a given string, with surrounding context.
 *  This is known as <em>keyword-in-context search</em>.
 */
public class KWIK {

    // Do not instantiate.
    private KWIK() { }

    /**
     * Reads a string from a file specified as the first
     * command-line argument; read an integer k specified as the
     * second command line argument; then repeatedly processes
     * use queries, printing all occurrences of the given query
     * string in the text string with k characters of surrounding
     * context on either side.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        int context = Integer.parseInt(args[1]);

        // read in text
        String text = in.readAll().replaceAll("\\s+", " ");
        int n = text.length();

        // build suffix array
        SuffixArray sa = new SuffixArray(text);

        // find all occurrences of queries and give context
        while (StdIn.hasNextLine()) {
            String query = StdIn.readLine();
            for (int i = sa.rank(query); i < n; i++) {
                int from1 = sa.index(i);
                int to1   = Math.min(n, from1 + query.length());
                if (!query.equals(text.substring(from1, to1))) break;
                int from2 = Math.max(0, sa.index(i) - context);
                int to2   = Math.min(n, sa.index(i) + context + query.length());
                StdOut.println(text.substring(from2, to2));
            }
            StdOut.println();
        }
    } 
}
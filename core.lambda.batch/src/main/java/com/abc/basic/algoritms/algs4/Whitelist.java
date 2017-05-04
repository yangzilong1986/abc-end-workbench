package com.abc.basic.algoritms.algs4;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdIn;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  The {@code Whitelist} class provides a client for reading in
 *  a set of integers from a file; reading in a sequence of integers
 *  from standard input; and printing to standard output those 
 *  integers not in the whitelist.
 */
public class Whitelist {

    // Do not instantiate.
    private Whitelist() { }

    public static void main(String[] args) {
        In in = new In(args[0]);
        int[] white = in.readAllInts();
        StaticSETofInts set = new StaticSETofInts(white);

        // Read key, print if not in whitelist.
        while (!StdIn.isEmpty()) {
            int key = StdIn.readInt();
            if (!set.contains(key))
                StdOut.println(key);
        }
    }
}
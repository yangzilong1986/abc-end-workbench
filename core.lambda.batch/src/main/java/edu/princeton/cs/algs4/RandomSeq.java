package edu.princeton.cs.algs4;

import edu.princeton.cs.algs4.utils.StdOut;

/**
 *  The {@code RandomSeq} class is a client that prints out a pseudorandom
 *  sequence of real numbers in a given range.
 */
public class RandomSeq { 

    // this class should not be instantiated
    private RandomSeq() { }


    /**
     * Reads in two command-line arguments lo and hi and prints n uniformly
     * random real numbers in [lo, hi) to standard output.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        // command-line arguments
        int n = Integer.parseInt(args[0]);

        // for backward compatibility with Intro to Programming in Java version of RandomSeq
        if (args.length == 1) {
            // generate and print n numbers between 0.0 and 1.0
            for (int i = 0; i < n; i++) {
                double x = StdRandom.uniform();
                StdOut.println(x);
            }
        }

        else if (args.length == 3) {
            double lo = Double.parseDouble(args[1]);
            double hi = Double.parseDouble(args[2]);

            // generate and print n numbers between lo and hi
            for (int i = 0; i < n; i++) {
                double x = StdRandom.uniform(lo, hi);
                StdOut.printf("%.2f\n", x);
            }
        }

        else {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
    }
}
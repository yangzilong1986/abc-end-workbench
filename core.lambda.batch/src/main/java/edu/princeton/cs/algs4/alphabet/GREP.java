package edu.princeton.cs.algs4.alphabet;

import edu.princeton.cs.algs4.alphabet.NFA;
import edu.princeton.cs.algs4.utils.StdIn;
import edu.princeton.cs.algs4.utils.StdOut;

/**
 *  The {@code GREP} class provides a client for reading in a sequence of
 *  lines from standard input and printing to standard output those lines
 *  that contain a substring matching a specified regular expression.
 */
public class GREP {

    // do not instantiate
    private GREP() { }

    /**
     * Interprets the command-line argument as a regular expression
     * (supporting closure, binary or, parentheses, and wildcard)
     * reads in lines from standard input; writes to standard output
     * those lines that contain a substring matching the regular
     * expression.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) { 
        String regexp = "(.*" + args[0] + ".*)";
        NFA nfa = new NFA(regexp);
        while (StdIn.hasNextLine()) {
            String line = StdIn.readLine();
            if (nfa.recognizes(line)) {
                StdOut.println(line);
            }
        }
    } 
}
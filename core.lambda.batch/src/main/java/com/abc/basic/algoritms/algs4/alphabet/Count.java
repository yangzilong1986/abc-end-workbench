package com.abc.basic.algoritms.algs4.alphabet;

import com.abc.basic.algoritms.algs4.utils.StdIn;
import com.abc.basic.algoritms.algs4.utils.StdOut;

public class Count {

    // Do not instantiate.
    private Count() { }

    public static void main(String[] args) {
        Alphabet alphabet = new Alphabet(args[0]);
        final int R = alphabet.radix();
        int[] count = new int[R];
        while (StdIn.hasNextChar()) {
            char c = StdIn.readChar();
            if (alphabet.contains(c))
                count[alphabet.toIndex(c)]++;
        }
        for (int c = 0; c < R; c++)
            StdOut.println(alphabet.toChar(c) + " " + count[c]);
    }
}
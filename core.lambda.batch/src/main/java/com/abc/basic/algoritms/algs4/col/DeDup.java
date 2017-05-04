package com.abc.basic.algoritms.algs4.col;

import com.abc.basic.algoritms.algs4.utils.StdIn;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 */
public class DeDup {  

    // Do not instantiate.
    private DeDup() { }

    public static void main(String[] args) {
        SET<String> set = new SET<String>();

        // read in strings and add to set
        while (!StdIn.isEmpty()) {
            String key = StdIn.readString();
            if (!set.contains(key)) {
                set.add(key);
                StdOut.println(key);
            }
        }
    }
}
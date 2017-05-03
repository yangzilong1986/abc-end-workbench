package edu.princeton.cs.algs4;

import edu.princeton.cs.algs4.utils.StdIn;
import edu.princeton.cs.algs4.utils.StdOut;

/**
 *  The {@code LongestRepeatedSubstring} class provides a {@link SuffixArray}
 *  client for computing the longest repeated substring of a string that
 *  appears at least twice. The repeated substrings may overlap (but must
 *  be distinct).
 */
public class LongestRepeatedSubstring {

    // Do not instantiate.
    private LongestRepeatedSubstring() { }

    /**
     * Returns the longest common string of the two specified strings.
     *
     * @param  s one string
     * @param  t the other string
     * @return the longest common string that appears as a substring
     */

    /**
     * Returns the longest repeated substring of the specified string.
     *
     * @param  text the string
     * @return the longest repeated substring that appears in {@code text};
     *         the empty string if no such string
     */
    public static String lrs(String text) {
        int n = text.length();
        SuffixArray sa = new SuffixArray(text);
        String lrs = "";
        for (int i = 1; i < n; i++) {
            int length = sa.lcp(i);
            if (length > lrs.length()) {
                // lrs = sa.select(i).substring(0, length);
                lrs = text.substring(sa.index(i), sa.index(i) + length);
            }
        }
        return lrs;
    }

    /**
     * Unit tests the {@code lrs()} method.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String text = StdIn.readAll().replaceAll("\\s+", " ");
        StdOut.println("'" + lrs(text) + "'");
    }
}
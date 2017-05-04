/******************************************************************************
 * Compilation:  javac SuffixArrayX.java
 * Execution:    java SuffixArrayX < input.txt
 * Dependencies: StdIn.java StdOut.java
 * Data files:   http://algs4.cs.princeton.edu/63suffix/abra.txt
 * <p>
 * A data type that computes the suffix array of a string using 3-way
 * radix quicksort.
 * <p>
 * % java SuffixArrayX < abra.txt
 * i ind lcp rnk  select
 * ---------------------------
 * 0  11   -   0  !
 * 1  10   0   1  A!
 * 2   7   1   2  ABRA!
 * 3   0   4   3  ABRACADABRA!
 * 4   3   1   4  ACADABRA!
 * 5   5   1   5  ADABRA!
 * 6   8   0   6  BRA!
 * 7   1   3   7  BRACADABRA!
 * 8   4   0   8  CADABRA!
 * 9   6   0   9  DABRA!
 * 10   9   0  10  RA!
 * 11   2   2  11  RACADABRA!
 ******************************************************************************/
package com.abc.basic.algoritms.algs4.alphabet;

import com.abc.basic.algoritms.algs4.utils.StdOut;

public class SuffixArrayX {
    private static final int CUTOFF = 5;   // cutoff to insertion sort (any value between 0 and 12)

    private final char[] text;
    private final int[] index;   // index[i] = j means text.substring(j) is ith largest suffix
    private final int n;         // number of characters in text

    public SuffixArrayX(String text) {
        n = text.length();
        text = text + '\0';
        this.text = text.toCharArray();
        this.index = new int[n];
        for (int i = 0; i < n; i++)
            index[i] = i;

        sort(0, n - 1, 0);
    }

    // 3-way string quicksort lo..hi starting at dth character
    private void sort(int lo, int hi, int d) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        char v = text[index[lo] + d];
        int i = lo + 1;
        //3-way string quicksort
        while (i <= gt) {
            char t = text[index[i] + d];
            if (t < v) {
                exch(lt++, i++);
            } else if (t > v) {
                exch(i, gt--);
            } else {
                i++;
            }
        }
        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(lo, lt - 1, d);
        if (v > 0) {
            sort(lt, gt, d + 1);
        }
        sort(gt + 1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private void insertion(int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(index[j], index[j - 1], d); j--) {
                exch(j, j - 1);
            }
        }
    }

    // is text[i+d..n) < text[j+d..n) ?
    private boolean less(int i, int j, int d) {
        if (i == j) return false;
        i = i + d;
        j = j + d;
        while (i < n && j < n) {
            if (text[i] < text[j]) {
                return true;
            }
            if (text[i] > text[j]) {
                return false;
            }
            i++;
            j++;
        }
        return i > j;
    }

    // exchange index[i] and index[j]
    private void exch(int i, int j) {
        int swap = index[i];
        index[i] = index[j];
        index[j] = swap;
    }

    /**
     * Returns the length of the input string.
     * @return the length of the input string
     */
    public int length() {
        return n;
    }

    public int index(int i) {
        if (i < 0 || i >= n) {
            throw new IndexOutOfBoundsException();
        }
        return index[i];
    }

    /**
     * 最长公共前缀
     * @param i
     * @return
     */
    public int lcp(int i) {
        if (i < 1 || i >= n) {
            throw new IndexOutOfBoundsException();
        }
        return lcp(index[i], index[i - 1]);
    }

    // longest common prefix of text[i..n) and text[j..n)
    private int lcp(int i, int j) {
        int length = 0;
        while (i < n && j < n) {
            if (text[i] != text[j]) {
                return length;
            }
            i++;
            j++;
            length++;
        }
        return length;
    }

    public String select(int i) {
        if (i < 0 || i >= n) {
            throw new IndexOutOfBoundsException();
        }
        return new String(text, index[i], n - index[i]);
    }

    public int rank(String query) {
        int lo = 0, hi = n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = compare(query, index[mid]);
            if (cmp < 0) {
                hi = mid - 1;
            } else if (cmp > 0) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return lo;
    }

    // is query < text[i..n) ?
    private int compare(String query, int i) {
        int m = query.length();
        int j = 0;
        while (i < n && j < m) {
            if (query.charAt(j) != text[i]) {
                return query.charAt(j) - text[i];
            }
            i++;
            j++;

        }
        if (i < n) {
            return -1;
        }
        if (j < m) {
            return +1;
        }
        return 0;
    }

    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        SuffixArrayX suffix1 = new SuffixArrayX(s);
        SuffixArray suffix2 = new SuffixArray(s);
        boolean check = true;
        for (int i = 0; check && i < s.length(); i++) {
            if (suffix1.index(i) != suffix2.index(i)) {
                StdOut.println("suffix1(" + i + ") = " + suffix1.index(i));
                StdOut.println("suffix2(" + i + ") = " + suffix2.index(i));
                String ith = "\"" + s.substring(suffix1.index(i), Math.min(suffix1.index(i) + 50, s.length())) + "\"";
                String jth = "\"" + s.substring(suffix2.index(i), Math.min(suffix2.index(i) + 50, s.length())) + "\"";
                StdOut.println(ith);
                StdOut.println(jth);
                check = false;
            }
        }

        StdOut.println("  i ind lcp rnk  select");
        StdOut.println("---------------------------");

        for (int i = 0; i < s.length(); i++) {
            int index = suffix2.index(i);
            String ith = "\"" + s.substring(index, Math.min(index + 50, s.length())) + "\"";
            int rank = suffix2.rank(s.substring(index));
            assert s.substring(index).equals(suffix2.select(i));
            if (i == 0) {
                StdOut.printf("%3d %3d %3s %3d  %s\n", i, index, "-", rank, ith);
            } else {
                // int lcp  = suffix.lcp(suffix2.index(i), suffix2.index(i-1));
                int lcp = suffix2.lcp(i);
                StdOut.printf("%3d %3d %3d %3d  %s\n", i, index, lcp, rank, ith);
            }
        }
    }

}
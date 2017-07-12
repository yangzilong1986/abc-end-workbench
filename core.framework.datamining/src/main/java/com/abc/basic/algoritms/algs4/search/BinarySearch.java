package com.abc.basic.algoritms.algs4.search;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdIn;
import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Arrays;

public class BinarySearch<Key extends Comparable> {

    /**
     * This class should not be instantiated.
     */
    public BinarySearch() { }

    public int indexOf(Key[] a, Key key) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            int cmp=key.compareTo(a[mid]);
            if (cmp<0) {
                hi = mid - 1;
            }
            else if (cmp>0) {
                lo = mid + 1;
            }
            else {
                return mid;
            }
        }
        return -1;
    }

    @Deprecated
    public int rank(Key key, Key[] a) {
        return indexOf(a, key);
    }

    public static void main(String[] args) {
        String[] labels =new String[]{"age", "presscript", "astigmatic", "tearRate"};
        BinarySearch bs=new BinarySearch<String>();
        bs.indexOf(labels,"astigmatic");
    }
}



package com.abc.basic.algoritms.algs4.alphabet;

import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  高位优先排序
 */
public class MSD {
    //
    private static final int BITS_PER_BYTE =   8;
    private static final int BITS_PER_INT  =  32;   // each Java int is 32 bits
    //基数
    private static final int R = 256;   // extended ASCII alphabet size
    //数组的切割阀值
    private static final int CUTOFF =  15;   // cutoff to insertion sort

    private MSD() { }

    private static int charAt(String s, int d) {
        assert d >= 0 && d <= s.length();
        if (d == s.length()) {
            return -1;
        }
        return s.charAt(d);
    }

    public static void sort(String[] a) {
        int n = a.length;
        //数据分类的辅助数组
        String[] aux = new String[n];
        sort(a, 0, n-1, 0, aux);
    }

    private static void sort(String[] a, int lo, int hi, int d, String[] aux) {

        // cutoff to insertion sort for small subarrays
        //以d个字符为键将a[lo]到a[hi]排序
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        // compute frequency counts
        //计算频率
        int[] count = new int[R+2];
        //初始化运行，i为0，hi为输入的数组个数length-1
        for (int i = lo; i <= hi; i++) {//带排序的每个元素的第0个字符的数量
            int c = charAt(a[i], d);
            count[c+2]++;
        }
        //将频率转换为索引
        for (int r = 0; r < R+1; r++) {
            count[r + 1] += count[r];
        }
        // distribute
        //数据分类
        for (int i = lo; i <= hi; i++) {
            int c = charAt(a[i], d);
            aux[count[c+1]++] = a[i];
        }
        //回写
        for (int i = lo; i <= hi; i++) {
            a[i] = aux[i - lo];
        }
        //递归的以每个字符为键进行排序
        for (int r = 0; r < R; r++) {
            //d为位数
            //sort( a,   int lo,          int hi,         int d, aux)
            sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
        }
    }

    // insertion sort a[lo..hi], starting at dth character
    private static void insertion(String[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(a[j], a[j - 1], d); j--) {
                exch(a, j, j - 1);
            }
        }
    }

    // exchange a[i] and a[j]
    private static void exch(String[] a, int i, int j) {
        String temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is v less than w, starting at character d
    private static boolean less(String v, String w, int d) {
        // assert v.substring(0, d).equals(w.substring(0, d));
        for (int i = d; i < Math.min(v.length(), w.length()); i++) {
            if (v.charAt(i) < w.charAt(i)) {
                return true;
            }
            if (v.charAt(i) > w.charAt(i)) {
                return false;
            }
        }
        return v.length() < w.length();
    }

    public static void main(String[] args) {
        String[] a ={"MMMM","TTTT","CCCF","UUUU","TTTR","ABCD","ABCG","BDDD","GGGG","ZZZZ",
                     "QQQQ","TTRR","CCGG","UUTT","TTEE","ABJJ","ABYG","BDDY","GGGA","ZZZA"
        };
        int n = a.length;
        sort(a);
        for (int i = 0; i < n; i++)
            StdOut.println(a[i]);
    }
}
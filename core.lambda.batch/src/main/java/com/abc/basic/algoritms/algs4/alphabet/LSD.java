package com.abc.basic.algoritms.algs4.alphabet;

import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  低位优先的字符串排序
 */
public class LSD {

    // do not instantiate
    private LSD() { }

    public static void sort(String[] a, int w) {
        int n = a.length;
//        int R = 256;   // extend ASCII alphabet size
        int R = 128;   // extend ASCII alphabet size
        String[] aux = new String[n];
        //从低位向前取值，d为位数
        for (int d = w-1; d >= 0; d--) {
            // sort by key-indexed counting on dth character
            // compute frequency counts
            int[] count = new int[R+1];

            for (int i = 0; i < n; i++) {//charAt为key方法
                char key=a[i].charAt(d);//取低位，即d分别为3,2，1,0
                count[key + 1]++;//对key进行计数
            }
            //频率转换为索引
            for (int r = 0; r < R; r++) {//R为分类的大小
                count[r + 1] += count[r];
            }
            //将元素分类
            for (int i = 0; i < n; i++) {
                char key=a[i].charAt(d);
                aux[count[key]++] = a[i];//count[key]的值++
            }
            //回写
            for (int i = 0; i < n; i++) {
                a[i] = aux[i];
            }
        }
    }


    public static void main(String[] args) {
        String[] a ={"MNAT","TYUT","CBAD","TRDB","FDFD","ABCD","ABCG","BDDD","GGGG","ZZZZ"};
        int n = a.length;

        // check that strings have fixed length
        int w = a[0].length();
       // sort the strings
        sort(a, w);

        // print results
        for (int i = 0; i < n; i++)
            StdOut.println(a[i]);
    }
}
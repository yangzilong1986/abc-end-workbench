package edu.princeton.cs.algs4.tree;

import edu.princeton.cs.algs4.utils.In;
import edu.princeton.cs.algs4.utils.StdIn;
import edu.princeton.cs.algs4.utils.StdOut;

public class SampleUF {
    // parent[i] = parent of i
    //分量ID，父连接数组，由触点索引
    private int[] id;
    // number of components
    private int count;
    public SampleUF(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        count = n;
        //初始化分量数组
        id = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
        }
    }

    public int find(int p) {
        validate(p);
        while (p != id[p]) {//不是根结点则循环
            p = id[p];
        }
        return p;
    }

    public int count() {
        return count;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public void union(int p, int q) {
        int rootP = find(p);//
        int rootQ = find(q);//查找
        if (rootP == rootQ){
            return;
        }
        id[rootP]=rootQ;
        count--;
    }

    // validate that p is a valid index
    private void validate(int p) {
        int n = id.length;
        if (p < 0 || p >= n) {
            throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + (n-1));
        }
    }

    public static void main(String[] args) {
        String PATH_NAME = In.PATH_NAME;
        In in = new In(PATH_NAME + "tinyUF.txt");
        int n = in.readInt();
        SampleUF uf = new SampleUF(n);
        while (!in.isEmpty()) {
            int p = in.readInt();
            int q = in.readInt();
            if (uf.connected(p, q)){
                continue;
            }
            uf.union(p, q);
            StdOut.println(p + " " + q);
        }
        StdOut.println(uf.count() + " components");
    }
}

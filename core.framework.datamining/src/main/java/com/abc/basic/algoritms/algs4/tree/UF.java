package com.abc.basic.algoritms.algs4.tree;


import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 动态连通性
 * p和q是联通的
 */
public class UF {
    // parent[i] = parent of i
    //分量ID，父连接数组，由触点索引
    private int[] parent;
    // rank[i] = rank of subtree rooted at i (never more than 31)
    //各个根节点所对应的分量大小
    private byte[] rank;
    // number of components
    private int count;

    public UF(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        count = n;
        //初始化分量数组
        parent = new int[n];
        rank = new byte[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int p) {
        validate(p);
        while (p != parent[p]) {
            // path compression by halving
            parent[p] = parent[parent[p]];
            p = parent[p];
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
        if (rootP == rootQ) {
            return;
        }

        // make root of smaller rank point to root of larger rank
        //将小树的根节点连接到大树的根节点
        //rank[rootQ]
        if (rank[rootP] < rank[rootQ]) {//如果P的根节点数量小于Q根节点数量，则把Q索引赋予P的索引
            parent[rootP] = rootQ;
        } else if (rank[rootP] > rank[rootQ]) {
            parent[rootQ] = rootP;
        } else {
            //两个元素索引相等时，则更新Q的索引为P的索引
            parent[rootQ] = rootP;
            //P、Q索引相同
            //rank[rootP]第一个元素数量增加一个
            rank[rootP]++;
        }
        count--;
    }

    // validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    public static void main(String[] args) {
        String PATH_NAME = In.PATH_NAME;
        In in = new In(PATH_NAME + "tinyUF.txt");
        int n = in.readInt();
        UF uf = new UF(n);
        while (!in.isEmpty()) {
            int p = in.readInt();
            int q = in.readInt();
            if (uf.connected(p, q)) {
                continue;
            }
            uf.union(p, q);
            StdOut.println(p + " " + q);
        }
        StdOut.println(uf.count() + " components");
    }
}
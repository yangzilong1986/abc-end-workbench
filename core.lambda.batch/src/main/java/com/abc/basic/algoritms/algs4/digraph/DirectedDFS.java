package com.abc.basic.algoritms.algs4.digraph;

import com.abc.basic.algoritms.algs4.col.Bag;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 图的深度优先解决可达性问题
 *
 * 有向图的可达性，单点联通性问题
 * 有向图的可达性
 * 多点可达性：
 *  给定一副有向图和顶点的集合，回答：
 *  是否存在一条从集合中的任意顶点到达给定节点v的有向路径
 */
public class DirectedDFS {
    //顶点是否被访问过
    private boolean[] marked;  // marked[v] = true if v is reachable
                               // from source (or sources)
    private int count;         // number of vertices reachable from s

    public DirectedDFS(Digraph G, int s) {
        marked = new boolean[G.V()];
        validateVertex(s);
        dfs(G, s);
    }

    public DirectedDFS(Digraph G, Iterable<Integer> sources) {
        marked = new boolean[G.V()];
        validateVertices(sources);
        for (int v : sources) {
            if (!marked[v]) {
                dfs(G, v);
            }
        }
    }

    private void dfs(Digraph G, int v) { 
        count++;
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                System.out.println("递归之前 一个边的顶点 v:"+v+" 遍历的另一个端点w:"+w);
                dfs(G, w);
                System.out.println("递归之后 一个边的顶点 v:"+v+" 遍历的另一个端点w:"+w);
            }
        }
    }

    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    public int count() {
        return count;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " +
                    v + " is not between 0 and " + (V - 1));
        }
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int V = marked.length;
        for (int v : vertices) {
            if (v < 0 || v >= V) {
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
            }
        }
    }


    public static void main(String[] args) {

        Digraph G =Digraph.buildDigraph();// new Digraph(in);
        // read in sources from command-line arguments
        Bag<Integer> sources = new Bag<Integer>();
//        int[] vv={1,2,6};
//        for (int i = 1; i < vv.length; i++) {
//            int s = vv[i];
//            sources.add(s);
//        }

        // multiple-source reachability
        DirectedDFS dfs = new DirectedDFS(G, 0);

        // print out vertices reachable from sources
        for (int v = 0; v < G.V(); v++) {
            if (dfs.marked(v)) StdOut.print(v + " ");
        }
        StdOut.println();
    }

}
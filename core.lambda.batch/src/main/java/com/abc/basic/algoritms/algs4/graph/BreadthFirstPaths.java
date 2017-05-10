package com.abc.basic.algoritms.algs4.graph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 单点最短路径
 * 给定一副图和一个起点，回答：
 * 从起点s到目标顶点v是否存在一条路径，如果有，则找出其中哪条最短，即边最少
 */
public class BreadthFirstPaths {
    private static final int INFINITY = Integer.MAX_VALUE;
    protected boolean[] marked;  // marked[v] = is there an s-v path
    //edgeTo[] 数组表示了一棵以起点为根结点的树
    protected int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    protected int[] distTo;      // distTo[v] = number of edges shortest s-v path

    public BreadthFirstPaths(Graph G, int s) {
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        validateVertex(s);
        bfs(G, s);
        assert new BreadthFirstPathsCheck(this).check(G, s);
    }

    // breadth-first search from a single source
    private void bfs(Graph G, int s) {
        Queue<Integer> q = new Queue<Integer>();
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = INFINITY;
        }
        distTo[s] = 0;
        marked[s] = true;// 标记起点
        q.enqueue(s);// 将它加入队列

        while (!q.isEmpty()) {
            // 从队列中删去下一顶点
            int v = q.dequeue();//出队
            for (int w : G.adj(v)) {//属于边，连接表为Bag实现了Iterable接口
                if (!marked[w]) {//对于每个未被标记的相邻顶点
                    //表明w的前前一个顶点为v
                    edgeTo[w] = v;//保存最短路径的最后一条边，v取端点0值，w取另一个端点2
                    distTo[w] = distTo[v] + 1;//顶点
                    marked[w] = true;//标记它，因为最短路径已知
                    q.enqueue(w);//并将它添加到队列中
                }
            }
        }
    }


    public boolean hasPathTo(int v) {
        validateVertex(v);
        return marked[v];
    }

    public int distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    public Iterable<Integer> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)){
            return null;
        }
        Stack<Integer> path = new Stack<Integer>();
        int x;
        for (x = v; distTo[x] != 0; x = edgeTo[x]) {
            path.push(x);
        }
        path.push(x);
        return path;
    }

    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " +
                    v + " is not between 0 and " + (V - 1));
        }
    }

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
        Graph G =Graph.buildGraph();
        int s =0;
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
        int vv=4;
        for (int x : bfs.pathTo(vv)) {
            if (x == s) StdOut.print(x);
            else        StdOut.print("-" + x);
        }
    }
}

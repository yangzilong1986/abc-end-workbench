package com.abc.basic.algoritms.algs4.graph;

import com.abc.basic.algoritms.algs4.StdRandom;
import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  欧拉回路和欧拉路径的判断
 *  欧拉回路：
 *  无向图：每个顶点的度数都是偶数，则存在欧拉回路。
 *  有向图：每个顶点的入度都等于出度，则存在欧拉回路。
 *
 *  欧拉路径：
 *  无向图：当且仅当该图所有顶点的度数为偶数 或者 除了两个度数为奇数外其余的全是偶数。
 *  有向图：当且仅当该图所有顶点 出度=入度 或者 一个顶点 出度=入度+1，另一个顶点入度=出度+1，
 *  其他顶点 出度=入度。
 *
 *  /////////////////////////////////////////////////////////////////
 *  a） 如果一个图是连通图（connected），并且每一个点的度都为偶数，那么这个图一定可以一笔画成，同时最终点就是起始点。
 *  b） 如果一个图是连通图（connected），但是有且只有两个点的度为奇数，那么图一定可以一笔画成，但是最终点不是起始点。
 *  c） 如果一个图是连通图（connected），但是一个点的度为奇数或者超过两个点的度为奇数，那么图一定不可以一笔画成.
 *
 */
public class EulerianPath {
    private Stack<Integer> path = null;   // Eulerian path; null if no suh path

    // an undirected edge, with a field to indicate whether the edge has already been used
    private static class Edge {
        private final int v;
        private final int w;
        private boolean isUsed;

        public Edge(int v, int w) {
            this.v = v;
            this.w = w;
            isUsed = false;
        }

        // returns the other vertex of the edge
        public int other(int vertex) {
            if      (vertex == v) {
                return w;
            }
            else if (vertex == w) {
                return v;
            }
            else throw new IllegalArgumentException("Illegal endpoint");
        }
    }

    public EulerianPath(Graph G) {

        // find vertex from which to start potential Eulerian path:
        // a vertex v with odd degree(v) if it exits;
        // otherwise a vertex with degree(v) > 0
        int oddDegreeVertices = 0;
        int s = nonIsolatedVertex(G);
        for (int v = 0; v < G.V(); v++) {
            //% 取余数,   比如10%3 结果为1
            if (G.degree(v) % 2 != 0) {//度为奇数
                oddDegreeVertices++;
                s = v;
            }
        }

        // graph can't have an Eulerian path
        // (this condition is needed for correctness)
        //奇数大于2时则不含有欧拉路径
        if (oddDegreeVertices > 2) {
            return;
        }

        // special case for graph with zero edges (has a degenerate Eulerian path)
        if (s == -1) {
            s = 0;
        }

        // create local view of adjacency lists, to iterate one vertex at a time
        // the helper Edge data type is used to avoid exploring both copies of an edge v-w
        Queue<Edge>[] adj = (Queue<Edge>[]) new Queue[G.V()];
        for (int v = 0; v < G.V(); v++) {
            adj[v] = new Queue<Edge>();
        }
        //每个顶点遍历
        for (int v = 0; v < G.V(); v++) {
            int selfLoops = 0;
            for (int w : G.adj(v)) {//每个顶点连接的另一个顶点
                // careful with self loops
                if (v == w) {//自连接
                    if (selfLoops % 2 == 0) {
                        Edge e = new Edge(v, w);
                        adj[v].enqueue(e);
                        adj[w].enqueue(e);
                    }
                    selfLoops++;
                } else if (v < w) {//
                    Edge e = new Edge(v, w);
                    adj[v].enqueue(e);
                    adj[w].enqueue(e);
                }
            }
        }

        // initialize stack with any non-isolated vertex
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(s);

        // 深度优先遍历
        path = new Stack<Integer>();
        while (!stack.isEmpty()) {
            int v = stack.pop();
            while (!adj[v].isEmpty()) {
                Edge edge = adj[v].dequeue();
                if (edge.isUsed) {
                    continue;
                }
                edge.isUsed = true;
                stack.push(v);
                v = edge.other(v);
            }
            // push vertex with no more leaving edges to path
            path.push(v);
        }

        // check if all edges are used
        //路径的尺寸等于边加1
        if (path.size() != G.E() + 1)
            path = null;

        assert certifySolution(G);
    }

    public Iterable<Integer> path() {
        return path;
    }

    public boolean hasEulerianPath() {
        return path != null;
    }


    // returns any non-isolated vertex; -1 if no such vertex
    private static int nonIsolatedVertex(Graph G) {
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0) {
                return v;
            }
        }
        return -1;
    }


    /**************************************************************************
     *
     *  The code below is solely for testing correctness of the data type.
     *
     **************************************************************************/
    private static boolean hasEulerianPath(Graph G) {
        if (G.E() == 0) return true;

        // Condition 1: degree(v) is even except for possibly two
        int oddDegreeVertices = 0;
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) % 2 != 0) {
                oddDegreeVertices++;
            }
        }
        if (oddDegreeVertices > 2) {
            return false;
        }

        // Condition 2: graph is connected, ignoring isolated vertices
        int s = nonIsolatedVertex(G);
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0 && !bfs.hasPathTo(v)) {
                return false;
            }
        }
        return true;
    }

    // check that solution is correct
    private boolean certifySolution(Graph G) {

        // internal consistency check
        if (hasEulerianPath() == (path() == null)) {
            return false;
        }

        // hashEulerianPath() returns correct value
        if (hasEulerianPath() != hasEulerianPath(G)) {
            return false;
        }

        // nothing else to check if no Eulerian path
        if (path == null) {
            return true;
        }

        // check that path() uses correct number of edges
        if (path.size() != G.E() + 1) {
            return false;
        }

        return true;
    }


    private static void unitTest(Graph G, String description) {
        StdOut.println(description);
        StdOut.println("-------------------------------------");
        StdOut.print(G);

        EulerianPath euler = new EulerianPath(G);

        StdOut.print("Eulerian path:  ");
        if (euler.hasEulerianPath()) {
            for (int v : euler.path()) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
        else {
            StdOut.println("none");
        }
        StdOut.println();
    }


    /**
     * Unit tests the {@code EulerianPath} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int V = 6;
        int E =8;

       // Eulerian cycle
        Graph G1 = GraphGenerator.eulerianCycle(V, E);
        unitTest(G1, "Eulerian cycle");

    }
}
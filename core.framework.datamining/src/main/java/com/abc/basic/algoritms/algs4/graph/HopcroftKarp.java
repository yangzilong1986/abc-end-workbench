package com.abc.basic.algoritms.algs4.graph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Iterator;

/**
 *  The {@code HopcroftKarp} class represents a data type for computing a
 *  <em>maximum (cardinality) matching</em> and a
 *  <em>minimum (cardinality) vertex cover</em> in a bipartite graph.
 *
 *  A <em>bipartite graph</em> in a graph whose vertices can be partitioned
 *  into two disjoint sets such that every edge has one endpoint in either set.
 *  A <em>matching</em> in a graph is a subset of its edges with no common
 *  vertices. A <em>maximum matching</em> is a matching with the maximum number
 *  of edges.
 *  A <em>perfect matching</em> is a matching which matches all vertices in the graph.
 *  A <em>vertex cover</em> in a graph is a subset of its vertices such that
 *  every edge is incident to at least one vertex. A <em>minimum vertex cover</em>
 *  is a vertex cover with the minimum number of vertices.
 *  By Konig's theorem, in any biparite
 *  graph, the maximum number of edges in matching equals the minimum number
 *  of vertices in a vertex cover.
 *  The maximum matching problem in <em>nonbipartite</em> graphs is
 *  also important, but all known algorithms for this more general problem
 *  are substantially more complicated.
 *  <p>
 *  This implementation uses the <em>Hopcroft-Karp algorithm</em>.
 *  The order of growth of the running time in the worst case is
 *  (<em>E</em> + <em>V</em>) sqrt(<em>V</em>),
 *  where <em>E</em> is the number of edges and <em>V</em> is the number
 *  of vertices in the graph. It uses extra space (not including the graph)
 *  proportional to <em>V</em>.
 *
 *  二分图
 *
 *   首先介绍一下题意：已知，有N个学生和P门课程，每个学生可以选0门，1门或者多门课程，
 *   要求在N个学生中选出P个学生使得这P个学生与P门课程一一对应。
 *   这个问题既可以利用最大流算法解决也可以用匈牙利算法解决。如果用最大流算法中的Edmonds-karp算法解决，
 *   因为时间复杂度为O(n*m*m),n为点数，m为边数，会超时，利用匈牙利算法，时间复杂度为O(n*m)，时间复杂度小，不会超时。
 *   其实匈牙利算法就是最大流算法，只不过它的使用范围仅限于二分图，所以可以称之为“二分图定制版的最大流算法”，
 *   既然是定制的，那么他就会考虑到二分图的特殊性，优化原来的最大流算法，降低时间复杂度，
 *   同时也变得有点复杂不容易理解了。既然匈牙利算法继承自最大流算法
 */
public class HopcroftKarp {
    private static final int UNMATCHED = -1;

    private final int V;                 // number of vertices in the graph
    private BipartiteX bipartition;      // the bipartition
    private int cardinality;             // cardinality of current matching
    private int[] mate;                  // mate[v] =  w if v-w is an edge in current matching
                                         //         = -1 if v is not in current matching
    private boolean[] inMinVertexCover;  // inMinVertexCover[v] = true iff v is in min vertex cover
    private boolean[] marked;            // marked[v] = true iff v is reachable via alternating path
    private int[] distTo;                // distTo[v] = number of edges on shortest path to v

    /**
     * Determines a maximum matching (and a minimum vertex cover)
     * in a bipartite graph.
     *
     * @param  G the bipartite graph
     * @throws IllegalArgumentException if {@code G} is not bipartite
     */
    public HopcroftKarp(Graph G) {
        bipartition = new BipartiteX(G);
        if (!bipartition.isBipartite()) {
            throw new IllegalArgumentException("graph is not bipartite");
        }

        // initialize empty matching
        this.V = G.V();
        mate = new int[V];
        for (int v = 0; v < V; v++)
            mate[v] = UNMATCHED;

        // the call to hasAugmentingPath() provides enough info to reconstruct level graph
        while (hasAugmentingPath(G)) {

            // to be able to iterate over each adjacency list, keeping track of which
            // vertex in each adjacency list needs to be explored next
            Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[G.V()];
            for (int v = 0; v < G.V(); v++)
                adj[v] = G.adj(v).iterator();

            // for each unmatched vertex s on one side of bipartition
            for (int s = 0; s < V; s++) {
                if (isMatched(s) || !bipartition.color(s)) continue;   // or use distTo[s] == 0

                // find augmenting path from s using nonrecursive DFS
                Stack<Integer> path = new Stack<Integer>();
                path.push(s);
                while (!path.isEmpty()) {
                    int v = path.peek();

                    // retreat, no more edges in level graph leaving v
                    if (!adj[v].hasNext())
                        path.pop();

                    // advance
                    else {
                        // process edge v-w only if it is an edge in level graph
                        int w = adj[v].next();
                        if (!isLevelGraphEdge(v, w)) continue;

                        // add w to augmenting path
                        path.push(w);

                        // augmenting path found: update the matching
                        if (!isMatched(w)) {
                            // StdOut.println("augmenting path: " + toString(path));

                            while (!path.isEmpty()) {
                                int x = path.pop();
                                int y = path.pop();
                                mate[x] = y;
                                mate[y] = x;
                            }
                            cardinality++;
                        }
                    }
                }
            }
        }

        // also find a min vertex cover
        inMinVertexCover = new boolean[V];
        for (int v = 0; v < V; v++) {
            if (bipartition.color(v) && !marked[v]) inMinVertexCover[v] = true;
            if (!bipartition.color(v) && marked[v]) inMinVertexCover[v] = true;
        }

        assert certifySolution(G);
    }

    // string representation of augmenting path (chop off last vertex)
    private static String toString(Iterable<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int v : path)
            sb.append(v + "-");
        String s = sb.toString();
        s = s.substring(0, s.lastIndexOf('-'));
        return s;
    }

   // is the edge v-w in the level graph?
    private boolean isLevelGraphEdge(int v, int w) {
        return (distTo[w] == distTo[v] + 1) && isResidualGraphEdge(v, w);
    }

   // is the edge v-w a forward edge not in the matching or a reverse edge in the matching?
    private boolean isResidualGraphEdge(int v, int w) {
        if ((mate[v] != w) &&  bipartition.color(v)) return true;
        if ((mate[v] == w) && !bipartition.color(v)) return true;
        return false;
    }

    /*
     * is there an augmenting path?
     *   - if so, upon termination adj[] contains the level graph;
     *   - if not, upon termination marked[] specifies those vertices reachable via an alternating
     *     path from one side of the bipartition
     *
     * an alternating path is a path whose edges belong alternately to the matching and not
     * to the matching
     *
     * an augmenting path is an alternating path that starts and ends at unmatched vertices
     */
    private boolean hasAugmentingPath(Graph G) {

        // shortest path distances
        marked = new boolean[V];
        distTo = new int[V];
        for (int v = 0; v < V; v++)
            distTo[v] = Integer.MAX_VALUE;

        // breadth-first search (starting from all unmatched vertices on one side of bipartition)
        Queue<Integer> queue = new Queue<Integer>();
        for (int v = 0; v < V; v++) {
            if (bipartition.color(v) && !isMatched(v)) {
                queue.enqueue(v);
                marked[v] = true;
                distTo[v] = 0;
            }
        }

        // run BFS until an augmenting path is found
        // (and keep going until all vertices at that distance are explored)
        boolean hasAugmentingPath = false;
        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            for (int w : G.adj(v)) {

                // forward edge not in matching or backwards edge in matching
                if (isResidualGraphEdge(v, w)) {
                    if (!marked[w]) {
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                        if (!isMatched(w))
                            hasAugmentingPath = true;

                        // stop enqueuing vertices once an alternating path has been discovered
                        // (no vertex on same side will be marked if its shortest path distance longer)
                        if (!hasAugmentingPath) queue.enqueue(w);
                    }
                }
            }
        }

        return hasAugmentingPath;
    }

    public int mate(int v) {
        validate(v);
        return mate[v];
    }

    public boolean isMatched(int v) {
        validate(v);
        return mate[v] != UNMATCHED;
    }

    /**
     * Returns the number of edges in any maximum matching.
     *
     * @return the number of edges in any maximum matching
     */
    public int size() {
        return cardinality;
    }


    public boolean isPerfect() {
        return cardinality * 2 == V;
    }

    /**
     * Returns true if the specified vertex is in the minimum vertex cover
     * computed by the algorithm.
     *
     * @param  v the vertex
     * @return {@code true} if vertex {@code v} is in the minimum vertex cover;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean inMinVertexCover(int v) {
        validate(v);
        return inMinVertexCover[v];
    }

    // throw an exception if vertex is invalid
    private void validate(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**************************************************************************
     *   
     *  The code below is solely for testing correctness of the data type.
     *
     **************************************************************************/

    // check that mate[] and inVertexCover[] define a max matching and min vertex cover, respectively
    private boolean certifySolution(Graph G) {

        // check that mate(v) = w iff mate(w) = v
        for (int v = 0; v < V; v++) {
            if (mate(v) == -1) continue;
            if (mate(mate(v)) != v) return false;
        }

        // check that size() is consistent with mate()
        int matchedVertices = 0;
        for (int v = 0; v < V; v++) {
            if (mate(v) != -1) matchedVertices++;
        }
        if (2*size() != matchedVertices) return false;

        // check that size() is consistent with minVertexCover()
        int sizeOfMinVertexCover = 0;
        for (int v = 0; v < V; v++)
            if (inMinVertexCover(v)) sizeOfMinVertexCover++;
        if (size() != sizeOfMinVertexCover) return false;

        // check that mate() uses each vertex at most once
        boolean[] isMatched = new boolean[V];
        for (int v = 0; v < V; v++) {
            int w = mate[v];
            if (w == -1) continue;
            if (v == w) return false;
            if (v >= w) continue;
            if (isMatched[v] || isMatched[w]) return false;
            isMatched[v] = true;
            isMatched[w] = true;
        }

        // check that mate() uses only edges that appear in the graph
        for (int v = 0; v < V; v++) {
            if (mate(v) == -1) continue;
            boolean isEdge = false;
            for (int w : G.adj(v)) {
                if (mate(v) == w) isEdge = true;
            }
            if (!isEdge) return false;
        }

        // check that inMinVertexCover() is a vertex cover
        for (int v = 0; v < V; v++)
            for (int w : G.adj(v))
                if (!inMinVertexCover(v) && !inMinVertexCover(w)) return false;

        return true;
    }

    public static void main(String[] args) {

        int V1 =6;
        int V2 = 8;
        int E  = 20;
        Graph G = GraphGenerator.bipartite(V1, V2, E);
        if (G.V() < 1000) {
            StdOut.println(G);
        }

        HopcroftKarp matching = new HopcroftKarp(G);

        // print maximum matching
        StdOut.printf("Number of edges in max matching        = %d\n", matching.size());
        StdOut.printf("Number of vertices in min vertex cover = %d\n", matching.size());
        StdOut.printf("Graph has a perfect matching           = %b\n", matching.isPerfect());
        StdOut.println();

        if (G.V() >= 1000) {
            return;
        }

        StdOut.print("Max matching: ");
        for (int v = 0; v < G.V(); v++) {
            int w = matching.mate(v);
            if (matching.isMatched(v) && v < w)  // print each edge only once
                StdOut.print(v + "+" + w + " ");
        }
        StdOut.println();

        // print minimum vertex cover
        StdOut.print("Min vertex cover: ");
        for (int v = 0; v < G.V(); v++)
            if (matching.inMinVertexCover(v))
                StdOut.print(v + "-");
        StdOut.println();
    }

}
package edu.princeton.cs.algs4.graph;

import edu.princeton.cs.algs4.utils.StdIn;
import edu.princeton.cs.algs4.utils.StdOut;

/**
 * 间隔的度数
 * 这段代码使用了SymbolGraph 和BreadthFirstPath 来查找图中的最短路径。
 * 对于movies.txt，可以用它来玩Kevin Bacon 游戏。
 */
public class DegreesOfSeparation {

    // this class cannot be instantiated
    private DegreesOfSeparation() { }

    public static void main(String[] args) {
        String filename  = args[0];
        String delimiter = args[1];
        String source    = args[2];

        // StdOut.println("Source: " + source);

        SymbolGraph sg = new SymbolGraph(filename, delimiter);
        Graph G = sg.graph();
        if (!sg.contains(source)) {
            StdOut.println(source + " not in database.");
            return;
        }

        int s = sg.indexOf(source);
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);

        while (!StdIn.isEmpty()) {
            String sink = StdIn.readLine();
            if (sg.contains(sink)) {
                int t = sg.indexOf(sink);
                if (bfs.hasPathTo(t)) {
                    for (int v : bfs.pathTo(t)) {
                        StdOut.println("   " + sg.nameOf(v));
                    }
                }
                else {
                    StdOut.println("Not connected");
                }
            }
            else {
                StdOut.println("   Not in database.");
            }
        }
    }
}
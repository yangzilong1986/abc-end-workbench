package edu.princeton.cs.algs4.graph;

import edu.princeton.cs.algs4.utils.StdOut;

public class BreadthFirstPathsCheck {
    final  BreadthFirstPaths breadthFirstPaths;
    BreadthFirstPathsCheck(BreadthFirstPaths breadthFirstPaths){
        this.breadthFirstPaths=breadthFirstPaths;
    }
    // check optimality conditions for single source
    boolean check(Graph G, int s) {

        // check that the distance of s = 0
        if (this.breadthFirstPaths.distTo[s] != 0) {
            StdOut.println("distance of source " + s + " to itself = " + this.breadthFirstPaths.distTo[s]);
            return false;
        }

        // check that for each edge v-w dist[w] <= dist[v] + 1
        // provided v is reachable from s
        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                if (this.breadthFirstPaths.hasPathTo(v) != this.breadthFirstPaths.hasPathTo(w)) {
                    StdOut.println("edge " + v + "-" + w);
                    StdOut.println("hasPathTo(" + v + ") = " + this.breadthFirstPaths.hasPathTo(v));
                    StdOut.println("hasPathTo(" + w + ") = " + this.breadthFirstPaths.hasPathTo(w));
                    return false;
                }
                if (this.breadthFirstPaths.hasPathTo(v) && (this.breadthFirstPaths.distTo[w] > this.breadthFirstPaths.distTo[v] + 1)) {
                    StdOut.println("edge " + v + "-" + w);
                    StdOut.println("distTo[" + v + "] = " + this.breadthFirstPaths.distTo[v]);
                    StdOut.println("distTo[" + w + "] = " + this.breadthFirstPaths.distTo[w]);
                    return false;
                }
            }
        }

        // check that v = edgeTo[w] satisfies distTo[w] = distTo[v] + 1
        // provided v is reachable from s
        for (int w = 0; w < G.V(); w++) {
            if (!this.breadthFirstPaths.hasPathTo(w) || w == s) continue;
            int v = this.breadthFirstPaths.edgeTo[w];
            if (this.breadthFirstPaths.distTo[w] != this.breadthFirstPaths.distTo[v] + 1) {
                StdOut.println("shortest path edge " + v + "-" + w);
                StdOut.println("distTo[" + v + "] = " + this.breadthFirstPaths.distTo[v]);
                StdOut.println("distTo[" + w + "] = " + this.breadthFirstPaths.distTo[w]);
                return false;
            }
        }

        return true;
    }
}

package edu.princeton.cs.algs4.graph;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 *  The {@code DegreesOfSeparation} class provides a client for finding
 *  the degree of separation between one distinguished individual and
 *  every other individual in a social network.
 *  As an example, if the social network consists of actors in which
 *  two actors are connected by a link if they appeared in the same movie,
 *  and Kevin Bacon is the distinguished individual, then the client
 *  computes the Kevin Bacon number of every actor in the network.
  */
public class DegreesOfSeparation {

    // this class cannot be instantiated
    private DegreesOfSeparation() { }

    /**
     *  Reads in a social network from a file, and then repeatedly reads in
     *  individuals from standard input and prints out their degrees of
     *  separation.
     *  Takes three command-line arguments: the name of a file,
     *  a delimiter, and the name of the distinguished individual.
     *  Each line in the file contains the name of a vertex, followed by a
     *  list of the names of the vertices adjacent to that vertex,
     *  separated by the delimiter.
     *
     * @param args the command-line arguments
     */
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
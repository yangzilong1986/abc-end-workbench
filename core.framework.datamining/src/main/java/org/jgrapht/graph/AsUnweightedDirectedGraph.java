package org.jgrapht.graph;

import org.jgrapht.*;

public class AsUnweightedDirectedGraph<V, E>
    extends AsUnweightedGraph<V, E>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 4999731801535663595L;

    public AsUnweightedDirectedGraph(DirectedGraph<V, E> g)
    {
        super(g);
    }
}

// End AsUnweightedDirectedGraph.java

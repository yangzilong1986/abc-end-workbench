package org.jgrapht.graph;

import java.io.*;

import org.jgrapht.*;

public class AsUnweightedGraph<V, E>
    extends GraphDelegator<V, E>
    implements Serializable
{
    private static final long serialVersionUID = 7175505077601824663L;

    /**
     * Constructor for AsUnweightedGraph.
     *
     * @param g the backing graph over which an unweighted view is to be created.
     */
    public AsUnweightedGraph(Graph<V, E> g)
    {
        super(g);
    }

    /**
     * @see Graph#getEdgeWeight
     */
    @Override
    public double getEdgeWeight(E e)
    {
        if (e == null) {
            throw new NullPointerException();
        } else {
            return WeightedGraph.DEFAULT_EDGE_WEIGHT;
        }
    }
}

// End AsUnweightedGraph.java

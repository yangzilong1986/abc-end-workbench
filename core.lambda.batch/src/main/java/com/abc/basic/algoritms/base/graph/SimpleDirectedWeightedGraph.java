package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class SimpleDirectedWeightedGraph<V, E>
        extends SimpleDirectedGraph<V, E>
        implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 3904960841681220919L;


    public SimpleDirectedWeightedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }

    /**
     * Creates a new simple directed weighted graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public SimpleDirectedWeightedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }


}

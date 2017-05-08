package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class SimpleWeightedGraph<V, E>
        extends SimpleGraph<V, E>
        implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 3906088949100655922L;

    /**
     * Creates a new simple weighted graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public SimpleWeightedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }

    /**
     * Creates a new simple weighted graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public SimpleWeightedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }


}

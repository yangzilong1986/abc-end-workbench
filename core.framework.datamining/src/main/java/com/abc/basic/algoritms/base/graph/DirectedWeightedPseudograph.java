package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class DirectedWeightedPseudograph<V, E>
        extends DirectedPseudograph<V, E>
        implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 8762514879586423517L;

    /**
     * Creates a new directed weighted pseudograph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public DirectedWeightedPseudograph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    /**
     * Creates a new directed weighted pseudograph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public DirectedWeightedPseudograph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }


}


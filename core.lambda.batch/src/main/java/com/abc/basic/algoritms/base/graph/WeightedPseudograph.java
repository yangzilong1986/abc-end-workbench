package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class WeightedPseudograph<V, E>
        extends Pseudograph<V, E>
        implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 3257290244524356152L;

    /**
     * Creates a new weighted pseudograph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public WeightedPseudograph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }

    /**
     * Creates a new weighted pseudograph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public WeightedPseudograph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }


}


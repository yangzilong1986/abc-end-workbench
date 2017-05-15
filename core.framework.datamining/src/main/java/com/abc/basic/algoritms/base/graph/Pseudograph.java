package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class Pseudograph<V, E>
        extends AbstractBaseGraph<V, E>
        implements UndirectedGraph<V, E>
{
    private static final long serialVersionUID = 3833183614484755253L;

    /**
     * Creates a new pseudograph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public Pseudograph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    /**
     * Creates a new pseudograph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public Pseudograph(EdgeFactory<V, E> ef)
    {
        super(ef, true, true);
    }


}

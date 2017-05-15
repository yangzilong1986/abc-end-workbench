package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class DirectedPseudograph<V, E>
        extends AbstractBaseGraph<V, E>
        implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = -8300409752893486415L;

    public DirectedPseudograph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    public DirectedPseudograph(EdgeFactory<V, E> ef)
    {
        super(ef, true, true);
    }

}


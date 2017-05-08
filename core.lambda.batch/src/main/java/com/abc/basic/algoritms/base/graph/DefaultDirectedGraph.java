package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;

public class DefaultDirectedGraph<V, E>
        extends AbstractBaseGraph<V, E>
        implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 3544953246956466230L;

    public DefaultDirectedGraph(Class<? extends E> edgeClass)
    {   //DefaultEdge
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    public DefaultDirectedGraph(EdgeFactory<V, E> ef)
    {
        super(ef, false, true);
    }


}

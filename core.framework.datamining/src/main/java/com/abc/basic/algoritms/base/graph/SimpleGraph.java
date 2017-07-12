package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class SimpleGraph<V, E>
        extends AbstractBaseGraph<V, E>
        implements UndirectedGraph<V, E>
{
    private static final long serialVersionUID = 3545796589454112304L;


    public SimpleGraph(EdgeFactory<V, E> ef)
    {
        super(ef, false, false);
    }

    public SimpleGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }


}

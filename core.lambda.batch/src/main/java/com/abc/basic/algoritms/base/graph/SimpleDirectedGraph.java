package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class SimpleDirectedGraph<V, E>
        extends AbstractBaseGraph<V, E>
        implements DirectedGraph<V, E> {
    private static final long serialVersionUID = 4049358608472879671L;

    public SimpleDirectedGraph(Class<? extends E> edgeClass) {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    public SimpleDirectedGraph(EdgeFactory<V, E> ef) {
        super(ef, false, false);
    }


}

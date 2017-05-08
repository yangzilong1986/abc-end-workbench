package com.abc.basic.algoritms.base.graph.edgefactory;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

import java.io.Serializable;

public class ClassBasedEdgeFactory<V, E>
        implements EdgeFactory<V, E>, Serializable
{
    private static final long serialVersionUID = 3618135658586388792L;

    private final Class<? extends E> edgeClass;

    public ClassBasedEdgeFactory(Class<? extends E> edgeClass)
    {
        this.edgeClass = edgeClass;
    }

    public E createEdge(V source, V target)
    {
        try {
            return edgeClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Edge factory failed", ex);
        }
    }
}
package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeSetFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class UndirectedEdgeContainer<V, E>
        implements Serializable
{
    private static final long serialVersionUID = -6623207588411170010L;
    Set<E> vertexEdges;
    private transient Set<E> unmodifiableVertexEdges = null;

    UndirectedEdgeContainer(EdgeSetFactory<V, E> edgeSetFactory, V vertex)
    {
        vertexEdges = edgeSetFactory.createEdgeSet(vertex);
    }

    public Set<E> getUnmodifiableVertexEdges()
    {
        if (unmodifiableVertexEdges == null) {
            unmodifiableVertexEdges = Collections.unmodifiableSet(vertexEdges);
        }
        return unmodifiableVertexEdges;
    }

    public void addEdge(E e)
    {
        vertexEdges.add(e);
    }

    public int edgeCount()
    {
        return vertexEdges.size();
    }

    public void removeEdge(E e)
    {
        vertexEdges.remove(e);
    }
}


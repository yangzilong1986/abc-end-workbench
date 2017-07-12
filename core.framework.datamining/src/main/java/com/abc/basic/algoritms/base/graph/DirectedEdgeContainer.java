package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeSetFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class DirectedEdgeContainer<V, E>
        implements Serializable
{
    private static final long serialVersionUID = 7494242245729767106L;
    Set<E> incoming;
    Set<E> outgoing;
    private transient Set<E> unmodifiableIncoming = null;
    private transient Set<E> unmodifiableOutgoing = null;

    DirectedEdgeContainer(EdgeSetFactory<V, E> edgeSetFactory, V vertex)
    {
        incoming = edgeSetFactory.createEdgeSet(vertex);
        outgoing = edgeSetFactory.createEdgeSet(vertex);
    }

    public Set<E> getUnmodifiableIncomingEdges()
    {
        if (unmodifiableIncoming == null) {
            unmodifiableIncoming = Collections.unmodifiableSet(incoming);
        }

        return unmodifiableIncoming;
    }

    public Set<E> getUnmodifiableOutgoingEdges()
    {
        if (unmodifiableOutgoing == null) {
            unmodifiableOutgoing = Collections.unmodifiableSet(outgoing);
        }

        return unmodifiableOutgoing;
    }

    public void addIncomingEdge(E e)
    {
        incoming.add(e);
    }

    public void addOutgoingEdge(E e)
    {
        outgoing.add(e);
    }

    public void removeIncomingEdge(E e)
    {
        incoming.remove(e);
    }

    public void removeOutgoingEdge(E e)
    {
        outgoing.remove(e);
    }
}


package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;

public class BreadthFirstIterator<V, E>
    extends CrossComponentIterator<V, E, Object>
{
    private Deque<V> queue = new ArrayDeque<>();

    public BreadthFirstIterator(Graph<V, E> g)
    {
        this(g, null);
    }

    public BreadthFirstIterator(Graph<V, E> g, V startVertex)
    {
        super(g, startVertex);
    }

    @Override
    protected boolean isConnectedComponentExhausted()
    {
        return queue.isEmpty();
    }

    @Override
    protected void encounterVertex(V vertex, E edge)
    {
        putSeenData(vertex, null);
        queue.add(vertex);
    }

    @Override
    protected void encounterVertexAgain(V vertex, E edge)
    {
    }

    @Override
    protected V provideNextVertex()
    {
        return queue.removeFirst();
    }
}

// End BreadthFirstIterator.java

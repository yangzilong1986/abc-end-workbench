package com.abc.basic.algoritms.base.graph.events;

import com.abc.basic.algoritms.base.graph.Graph;

public interface ListenableGraph<V, E>
        extends Graph<V, E>
{
    /**
     * Adds the specified graph listener to this graph, if not already present.
     *
     * @param l the listener to be added.
     */
    public void addGraphListener(GraphListener<V, E> l);

    /**
     * Adds the specified vertex set listener to this graph, if not already present.
     *
     * @param l the listener to be added.
     */
    public void addVertexSetListener(VertexSetListener<V> l);

    /**
     * Removes the specified graph listener from this graph, if present.
     *
     * @param l the listener to be removed.
     */
    public void removeGraphListener(GraphListener<V, E> l);

    /**
     * Removes the specified vertex set listener from this graph, if present.
     *
     * @param l the listener to be removed.
     */
    public void removeVertexSetListener(VertexSetListener<V> l);
}


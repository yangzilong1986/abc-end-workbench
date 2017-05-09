package com.abc.basic.algoritms.base.graph.events;

public interface GraphListener<V, E>
        extends VertexSetListener<V>
{
    /**
     * Notifies that an edge has been added to the graph.
     *
     * @param e the edge event.
     */
    void edgeAdded(GraphEdgeChangeEvent<V, E> e);

    /**
     * Notifies that an edge has been removed from the graph.
     *
     * @param e the edge event.
     */
    void edgeRemoved(GraphEdgeChangeEvent<V, E> e);
}


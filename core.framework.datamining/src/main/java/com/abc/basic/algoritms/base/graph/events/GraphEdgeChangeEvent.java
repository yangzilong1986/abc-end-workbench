package com.abc.basic.algoritms.base.graph.events;

public class GraphEdgeChangeEvent<V, E>
        extends GraphChangeEvent
{
    private static final long serialVersionUID = 3618134563335844662L;

    public static final int BEFORE_EDGE_ADDED = 21;

    public static final int BEFORE_EDGE_REMOVED = 22;

    public static final int EDGE_ADDED = 23;

    public static final int EDGE_REMOVED = 24;

    protected E edge;

     protected V edgeSource;

    protected V edgeTarget;

    public GraphEdgeChangeEvent(Object eventSource, int type, E edge, V edgeSource, V edgeTarget)
    {
        super(eventSource, type);
        this.edge = edge;
        this.edgeSource = edgeSource;
        this.edgeTarget = edgeTarget;
    }

    public E getEdge()
    {
        return edge;
    }

    public V getEdgeSource()
    {
        return edgeSource;
    }

    public V getEdgeTarget()
    {
        return edgeTarget;
    }
}


package com.abc.basic.algoritms.base.graph.events;

public class GraphVertexChangeEvent<V>
        extends GraphChangeEvent
{
    private static final long serialVersionUID = 3690189962679104053L;

    public static final int BEFORE_VERTEX_ADDED = 11;

    public static final int BEFORE_VERTEX_REMOVED = 12;

    public static final int VERTEX_ADDED = 13;

    public static final int VERTEX_REMOVED = 14;

    protected V vertex;

    public GraphVertexChangeEvent(Object eventSource, int type, V vertex)
    {
        super(eventSource, type);
        this.vertex = vertex;
    }

    public V getVertex()
    {
        return vertex;
    }
}


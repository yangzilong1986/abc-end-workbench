package com.abc.basic.algoritms.base.graph.events;

public interface VertexSetListener<V>
        extends EventListener
{

    void vertexAdded(GraphVertexChangeEvent<V> e);

    void vertexRemoved(GraphVertexChangeEvent<V> e);
}

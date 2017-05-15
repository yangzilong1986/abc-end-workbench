package org.jgrapht.graph;

import org.jgrapht.*;

public class ClassBasedVertexFactory<V>
    implements VertexFactory<V>
{
    private final Class<? extends V> vertexClass;

    /**
     * Create a new class based vertex factory.
     * 
     * @param vertexClass the vertex class
     */
    public ClassBasedVertexFactory(Class<? extends V> vertexClass)
    {
        this.vertexClass = vertexClass;
    }

    /**
     * @see VertexFactory#createVertex()
     */
    @Override
    public V createVertex()
    {
        try {
            return this.vertexClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Vertex factory failed", e);
        }
    }
}

// End ClassBasedVertexFactory.java

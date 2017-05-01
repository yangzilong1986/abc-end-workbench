package org.jgrapht.graph;

import java.io.*;

import org.jgrapht.*;

public class ClassBasedEdgeFactory<V, E>
    implements EdgeFactory<V, E>, Serializable
{
    private static final long serialVersionUID = 3618135658586388792L;

    private final Class<? extends E> edgeClass;

    /**
     * Create a new class based edge factory.
     * 
     * @param edgeClass the edge class
     */
    public ClassBasedEdgeFactory(Class<? extends E> edgeClass)
    {
        this.edgeClass = edgeClass;
    }

    /**
     * @see EdgeFactory#createEdge(Object, Object)
     */
    @Override
    public E createEdge(V source, V target)
    {
        try {
            return edgeClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Edge factory failed", ex);
        }
    }
}

// End ClassBasedEdgeFactory.java

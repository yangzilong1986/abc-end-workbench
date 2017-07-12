package org.jgrapht.graph;

import java.util.*;
import java.util.function.*;

import org.jgrapht.*;

public class DirectedMaskSubgraph<V, E>
    extends MaskSubgraph<V, E>
    implements DirectedGraph<V, E>
{


    @Deprecated
    public DirectedMaskSubgraph(DirectedGraph<V, E> base, MaskFunctor<V, E> mask)
    {
        super(base, mask);
    }

    public DirectedMaskSubgraph(
        DirectedGraph<V, E> base, Predicate<V> vertexMask, Predicate<E> edgeMask)
    {
        super(base, vertexMask, edgeMask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        return incomingEdgesOf(vertex).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return new MaskEdgeSet<>(
            base, ((DirectedGraph<V, E>) base).incomingEdgesOf(vertex), vertexMask, edgeMask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        return outgoingEdgesOf(vertex).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return new MaskEdgeSet<>(
            base, ((DirectedGraph<V, E>) base).outgoingEdgesOf(vertex), vertexMask, edgeMask);
    }

}

// End DirectedMaskSubgraph.java

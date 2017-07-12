package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.DirectedGraph;

import java.util.Set;
import java.util.function.Predicate;

public class DirectedMaskSubgraph<V, E>
        extends MaskSubgraph<V, E>
        implements DirectedGraph<V, E>
{


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


package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.DirectedGraph;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectedSubgraph<V, E>
        extends Subgraph<V, E, DirectedGraph<V, E>>
        implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 3616445700507054133L;

    public DirectedSubgraph(
            DirectedGraph<V, E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset)
    {
        super(base, vertexSubset, edgeSubset);
    }

    public DirectedSubgraph(DirectedGraph<V, E> base, Set<? extends V> vertexSubset)
    {
        this(base, vertexSubset, null);
    }

    public DirectedSubgraph(DirectedGraph<V, E> base)
    {
        this(base, null, null);
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

        return base.incomingEdgesOf(vertex).stream().filter(e -> edgeSet.contains(e)).collect(
                Collectors.toCollection(() -> new LinkedHashSet<>()));
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

        return base.outgoingEdgesOf(vertex).stream().filter(e -> edgeSet.contains(e)).collect(
                Collectors.toCollection(() -> new LinkedHashSet<>()));
    }
}


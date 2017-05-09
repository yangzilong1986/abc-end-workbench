package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.util.TypeUtil;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

class MaskEdgeSet<V, E>
        extends AbstractSet<E>
        implements Serializable
{
    private static final long serialVersionUID = 4208908842850100708L;

    private final Graph<V, E> graph;
    private final Set<E> edgeSet;
    private final Predicate<V> vertexMask;
    private final Predicate<E> edgeMask;

    public MaskEdgeSet(
            Graph<V, E> graph, Set<E> edgeSet, Predicate<V> vertexMask, Predicate<E> edgeMask)
    {
        this.graph = graph;
        this.edgeSet = edgeSet;
        this.vertexMask = vertexMask;
        this.edgeMask = edgeMask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o)
    {
        if (!edgeSet.contains(o)) {
            return false;
        }
        E e = TypeUtil.uncheckedCast(o, null);

        return !edgeMask.test(e) && !vertexMask.test(graph.getEdgeSource(e))
                && !vertexMask.test(graph.getEdgeTarget(e));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator()
    {
        return edgeSet
                .stream()
                .filter(
                        e -> !edgeMask.test(e) && !vertexMask.test(graph.getEdgeSource(e))
                                && !vertexMask.test(graph.getEdgeTarget(e)))
                .iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return (int) edgeSet
                .stream()
                .filter(
                        e -> !edgeMask.test(e) && !vertexMask.test(graph.getEdgeSource(e))
                                && !vertexMask.test(graph.getEdgeTarget(e)))
                .count();
    }

}

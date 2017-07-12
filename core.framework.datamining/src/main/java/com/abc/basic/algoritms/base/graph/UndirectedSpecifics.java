package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeSetFactory;
import com.abc.basic.algoritms.base.graph.specifics.Specifics;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSet;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSetEdgeSetFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UndirectedSpecifics<V, E>
        implements Specifics<V, E>, Serializable
{
    private static final long serialVersionUID = 6494588405178655873L;
    private static final String NOT_IN_UNDIRECTED_GRAPH =
            "no such operation in an undirected graph";

    protected AbstractBaseGraph<V, E> abstractBaseGraph;
    protected Map<V, UndirectedEdgeContainer<V, E>> vertexMapUndirected;
    protected EdgeSetFactory<V, E> edgeSetFactory;

    public UndirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph)
    {
        this(abstractBaseGraph, new LinkedHashMap<>(), new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    public UndirectedSpecifics(
            AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, UndirectedEdgeContainer<V, E>> vertexMap)
    {
        this(abstractBaseGraph, vertexMap, new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    public UndirectedSpecifics(
            AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, UndirectedEdgeContainer<V, E>> vertexMap,
            EdgeSetFactory<V, E> edgeSetFactory)
    {
        this.abstractBaseGraph = abstractBaseGraph;
        this.vertexMapUndirected = vertexMap;
        this.edgeSetFactory = edgeSetFactory;
    }

    @Override
    public void addVertex(V v)
    {
        // add with a lazy edge container entry
        vertexMapUndirected.put(v, null);
    }

    @Override
    public Set<V> getVertexSet()
    {
        return vertexMapUndirected.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> edges = null;

        if (abstractBaseGraph.containsVertex(sourceVertex)
                && abstractBaseGraph.containsVertex(targetVertex))
        {
            edges = new ArrayUnenforcedSet<>();

            for (E e : getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal = isEqualsStraightOrInverted(sourceVertex, targetVertex, e);

                if (equal) {
                    edges.add(e);
                }
            }
        }

        return edges;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        if (abstractBaseGraph.containsVertex(sourceVertex)
                && abstractBaseGraph.containsVertex(targetVertex))
        {

            for (E e : getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal = isEqualsStraightOrInverted(sourceVertex, targetVertex, e);

                if (equal) {
                    return e;
                }
            }
        }

        return null;
    }

    private boolean isEqualsStraightOrInverted(Object sourceVertex, Object targetVertex, E e)
    {
        boolean equalStraight = sourceVertex.equals(abstractBaseGraph.getEdgeSource(e))
                && targetVertex.equals(abstractBaseGraph.getEdgeTarget(e));

        boolean equalInverted = sourceVertex.equals(abstractBaseGraph.getEdgeTarget(e))
                && targetVertex.equals(abstractBaseGraph.getEdgeSource(e));
        return equalStraight || equalInverted;
    }


    @Override
    public void addEdgeToTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).addEdge(e);

        if (!source.equals(target)) {
            getEdgeContainer(target).addEdge(e);
        }
    }

    @Override
    public int degreeOf(V vertex)
    {
        if (abstractBaseGraph.isAllowingLoops()) { // then we must count, and add loops twice

            int degree = 0;
            Set<E> edges = getEdgeContainer(vertex).vertexEdges;

            for (E e : edges) {
                if (abstractBaseGraph.getEdgeSource(e).equals(abstractBaseGraph.getEdgeTarget(e))) {
                    degree += 2;
                } else {
                    degree += 1;
                }
            }

            return degree;
        } else {
            return getEdgeContainer(vertex).edgeCount();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEdgeFromTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).removeEdge(e);

        if (!source.equals(target)) {
            getEdgeContainer(target).removeEdge(e);
        }
    }

    protected UndirectedEdgeContainer<V, E> getEdgeContainer(V vertex)
    {
        // abstractBaseGraph.assertVertexExist(vertex); //JK: I don't think we need this here. This
        // should have been verified upstream

        UndirectedEdgeContainer<V, E> ec = vertexMapUndirected.get(vertex);

        if (ec == null) {
            ec = new UndirectedEdgeContainer<>(edgeSetFactory, vertex);
            vertexMapUndirected.put(vertex, ec);
        }

        return ec;
    }
}

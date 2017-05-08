package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeSetFactory;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSet;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSetEdgeSetFactory;

import java.util.*;

public class FastLookupUndirectedSpecifics<V, E>
        extends UndirectedSpecifics<V, E>
{
    private static final long serialVersionUID = 225772727571597846L;

    /*
     * Maps a pair of vertices <u,v> to a set of edges {(u,v)}. In case of a multigraph, all edges
     * which touch both u,v are included in the set
     */
    protected Map<Pair<V, V>, ArrayUnenforcedSet<E>> touchingVerticesToEdgeMap;

    /**
     * Construct a new fast lookup undirected specifics.
     *
     * @param abstractBaseGraph the graph for which these specifics are for
     */
    public FastLookupUndirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph)
    {
        this(abstractBaseGraph, new LinkedHashMap<>(), new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    /**
     * Construct a new fast lookup undirected specifics.
     *
     * @param abstractBaseGraph the graph for which these specifics are for
     * @param vertexMap map for the storage of vertex edge sets
     */
    public FastLookupUndirectedSpecifics(
            AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, UndirectedEdgeContainer<V, E>> vertexMap)
    {
        this(abstractBaseGraph, vertexMap, new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    /**
     * Construct a new fast lookup undirected specifics.
     *
     * @param abstractBaseGraph the graph for which these specifics are for
     * @param vertexMap map for the storage of vertex edge sets
     * @param edgeSetFactory factory for the creation of vertex edge sets
     */
    public FastLookupUndirectedSpecifics(
            AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, UndirectedEdgeContainer<V, E>> vertexMap,
            EdgeSetFactory<V, E> edgeSetFactory)
    {
        super(abstractBaseGraph, vertexMap, edgeSetFactory);
        this.touchingVerticesToEdgeMap = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        if (abstractBaseGraph.containsVertex(sourceVertex)
                && abstractBaseGraph.containsVertex(targetVertex))
        {
            Set<E> edges =
                    touchingVerticesToEdgeMap.get(new UnorderedPair<>(sourceVertex, targetVertex));
            return edges == null ? Collections.emptySet() : new ArrayUnenforcedSet<>(edges);
        } else {
            return null;
        }
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        List<E> edges =
                touchingVerticesToEdgeMap.get(new UnorderedPair<>(sourceVertex, targetVertex));
        if (edges == null || edges.isEmpty())
            return null;
        else
            return edges.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdgeToTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).addEdge(e);

        // Add edge to touchingVerticesToEdgeMap for the UnorderedPair {u,v}
        Pair<V, V> vertexPair = new UnorderedPair<>(source, target);
        if (!touchingVerticesToEdgeMap.containsKey(vertexPair)) {
            ArrayUnenforcedSet<E> edgeSet = new ArrayUnenforcedSet<>();
            edgeSet.add(e);
            touchingVerticesToEdgeMap.put(vertexPair, edgeSet);
        } else
            touchingVerticesToEdgeMap.get(vertexPair).add(e);

        if (!source.equals(target)) { // If not a self loop
            getEdgeContainer(target).addEdge(e);
        }
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

        if (!source.equals(target))
            getEdgeContainer(target).removeEdge(e);

        // Remove the edge from the touchingVerticesToEdgeMap. If there are no more remaining edges
        // for a pair
        // of touching vertices, remove the pair from the map.
        Pair<V, V> vertexPair = new UnorderedPair<>(source, target);
        if (touchingVerticesToEdgeMap.containsKey(vertexPair)) {
            ArrayUnenforcedSet<E> edgeSet = touchingVerticesToEdgeMap.get(vertexPair);
            edgeSet.remove(e);
            if (edgeSet.isEmpty())
                touchingVerticesToEdgeMap.remove(vertexPair);
        }
    }

}
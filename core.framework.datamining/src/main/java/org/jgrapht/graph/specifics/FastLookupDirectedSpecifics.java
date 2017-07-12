package org.jgrapht.graph.specifics;

import java.util.*;

import org.jgrapht.alg.util.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

public class FastLookupDirectedSpecifics<V, E>
    extends DirectedSpecifics<V, E>
{
    private static final long serialVersionUID = 4089085208843722263L;

    protected Map<Pair<V, V>, ArrayUnenforcedSet<E>> touchingVerticesToEdgeMap;

    public FastLookupDirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph)
    {
        this(abstractBaseGraph, new LinkedHashMap<>(), new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    public FastLookupDirectedSpecifics(
        AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, DirectedEdgeContainer<V, E>> vertexMap)
    {
        this(abstractBaseGraph, vertexMap, new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    public FastLookupDirectedSpecifics(
        AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, DirectedEdgeContainer<V, E>> vertexMap,
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
            Set<E> edges = touchingVerticesToEdgeMap.get(new Pair<>(sourceVertex, targetVertex));
            return edges == null ? Collections.emptySet() : new ArrayUnenforcedSet<>(edges);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        List<E> edges = touchingVerticesToEdgeMap.get(new Pair<>(sourceVertex, targetVertex));
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

        getEdgeContainer(source).addOutgoingEdge(e);
        getEdgeContainer(target).addIncomingEdge(e);

        Pair<V, V> vertexPair = new Pair<>(source, target);
        if (!touchingVerticesToEdgeMap.containsKey(vertexPair)) {
            ArrayUnenforcedSet<E> edgeSet = new ArrayUnenforcedSet<>();
            edgeSet.add(e);
            touchingVerticesToEdgeMap.put(vertexPair, edgeSet);
        } else
            touchingVerticesToEdgeMap.get(vertexPair).add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEdgeFromTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).removeOutgoingEdge(e);
        getEdgeContainer(target).removeIncomingEdge(e);

        // Remove the edge from the touchingVerticesToEdgeMap. If there are no more remaining edges
        // for a pair
        // of touching vertices, remove the pair from the map.
        Pair<V, V> vertexPair = new Pair<>(source, target);
        if (touchingVerticesToEdgeMap.containsKey(vertexPair)) {
            ArrayUnenforcedSet<E> edgeSet = touchingVerticesToEdgeMap.get(vertexPair);
            edgeSet.remove(e);
            if (edgeSet.isEmpty())
                touchingVerticesToEdgeMap.remove(vertexPair);
        }
    }

}

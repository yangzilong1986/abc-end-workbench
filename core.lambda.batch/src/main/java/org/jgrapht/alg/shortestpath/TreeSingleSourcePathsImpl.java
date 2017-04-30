package org.jgrapht.alg.shortestpath;

import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.graph.*;

/**
 * An implementation of {@link SingleSourcePaths} which uses linear space.
 * 
 * <p>
 * This implementation uses the traditional representation of maintaining for each vertex the
 * predecessor in the shortest path tree. In order to keep space to linear, the paths are recomputed
 * in each invocation of the {@link #getPath(Object)} method. The complexity of
 */
public class TreeSingleSourcePathsImpl<V, E>
    implements SingleSourcePaths<V, E>, Serializable
{
    private static final long serialVersionUID = -5914007312734512847L;

    /**
     * The graph
     */
    protected Graph<V, E> g;

    /**
     * The source vertex
     */
    protected V source;

    /**
     * A map which keeps for each target vertex the predecessor edge and the total length of the
     * shortest path.
     */
    protected Map<V, Pair<Double, E>> map;

    /**
     * Construct a new instance.
     * 
     * @param g the graph
     * @param source the source vertex
     * @param distanceAndPredecessorMap a map which contains for each vertex the distance and the
     *        last edge that was used to discover the vertex. The map does not need to contain any
     *        entry for the source vertex. In case it does contain the predecessor at the source
     *        vertex must be null.
     */
    public TreeSingleSourcePathsImpl(
        Graph<V, E> g, V source, Map<V, Pair<Double, E>> distanceAndPredecessorMap)
    {
        this.g = Objects.requireNonNull(g, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.map = Objects
            .requireNonNull(distanceAndPredecessorMap, "Distance and predecessor map is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<V, E> getGraph()
    {
        return g;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getSourceVertex()
    {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWeight(V targetVertex)
    {
        Pair<Double, E> p = map.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return p.getFirst();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V targetVertex)
    {
        if (source.equals(targetVertex)) {
            return new GraphWalk<>(g, source, targetVertex, null, Collections.emptyList(), 0d);
        }

        LinkedList<E> edgeList = new LinkedList<>();

        V cur = targetVertex;
        Pair<Double, E> p = map.get(cur);
        if (p == null) {
            return null;
        }

        double weight = 0d;
        while (p != null && !p.equals(source)) {
            E e = p.getSecond();
            if (e == null) {
                break;
            }
            edgeList.addFirst(e);
            weight += g.getEdgeWeight(e);
            cur = Graphs.getOppositeVertex(g, e, cur);
            p = map.get(cur);
        }

        return new GraphWalk<>(g, source, targetVertex, null, edgeList, weight);
    }

}

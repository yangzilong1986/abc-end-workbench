package org.jgrapht.alg.shortestpath;

import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.graph.*;

public class ListSingleSourcePathsImpl<V, E>
    implements SingleSourcePaths<V, E>, Serializable
{
    private static final long serialVersionUID = -60070018446561686L;

    /**
     * The graph
     */
    protected Graph<V, E> graph;

    /**
     * The source vertex of all paths
     */
    protected V source;

    /**
     * One path per vertex
     */
    protected Map<V, GraphPath<V, E>> paths;

    /**
     * Construct a new instance.
     * 
     * @param graph the graph
     * @param source the source vertex
     * @param paths one path per target vertex
     */
    public ListSingleSourcePathsImpl(Graph<V, E> graph, V source, Map<V, GraphPath<V, E>> paths)
    {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.paths = Objects.requireNonNull(paths, "Paths are null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<V, E> getGraph()
    {
        return graph;
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
        GraphPath<V, E> p = paths.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return p.getWeight();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V targetVertex)
    {
        GraphPath<V, E> p = paths.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return new GraphWalk<>(
                    graph, source, targetVertex, Collections.singletonList(source),
                    Collections.emptyList(), 0d);
            } else {
                return null;
            }
        } else {
            return p;
        }
    }

}

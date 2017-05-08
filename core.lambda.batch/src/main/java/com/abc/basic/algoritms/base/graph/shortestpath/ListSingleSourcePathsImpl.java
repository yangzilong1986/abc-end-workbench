package com.abc.basic.algoritms.base.graph.shortestpath;


import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.GraphPath;
import com.abc.basic.algoritms.base.graph.GraphWalk;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ListSingleSourcePathsImpl<V, E>
        implements ShortestPathAlgorithm.SingleSourcePaths<V, E>, Serializable
{
    private static final long serialVersionUID = -60070018446561686L;

    protected Graph<V, E> graph;

    protected V source;

    protected Map<V, GraphPath<V, E>> paths;

    public ListSingleSourcePathsImpl(Graph<V, E> graph, V source, Map<V, GraphPath<V, E>> paths)
    {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.paths = Objects.requireNonNull(paths, "Paths are null");
    }

    @Override
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    @Override
    public V getSourceVertex()
    {
        return source;
    }

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


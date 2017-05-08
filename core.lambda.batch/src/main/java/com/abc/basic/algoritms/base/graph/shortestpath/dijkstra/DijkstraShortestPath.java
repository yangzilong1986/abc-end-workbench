package com.abc.basic.algoritms.base.graph.shortestpath.dijkstra;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.GraphPath;
import com.abc.basic.algoritms.base.graph.shortestpath.BaseShortestPathAlgorithm;

/**
 *
 * 带权重的有向单源路径问题，非负值
 */
public final class DijkstraShortestPath<V, E>
        extends BaseShortestPathAlgorithm<V, E>
{
    private final double radius;

    public DijkstraShortestPath(Graph<V, E> graph)
    {
        this(graph, Double.POSITIVE_INFINITY);
    }

    public DijkstraShortestPath(Graph<V, E> graph, double radius)
    {
        super(graph);
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }
        this.radius = radius;
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("graph must contain the sink vertex");
        }
        if (source.equals(sink)) {
            return createEmptyPath(source, sink);
        }

        DijkstraClosestFirstIterator<V, E> it =
                new DijkstraClosestFirstIterator<>(graph, source, radius);

        while (it.hasNext()) {
            V vertex = it.next();
            if (vertex.equals(sink)) {
                break;
            }
        }

        return it.getPaths().getPath(sink);
    }

    @Override
    public SingleSourcePaths<V, E> getPaths(V source)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }

        DijkstraClosestFirstIterator<V, E> it =
                new DijkstraClosestFirstIterator<>(graph, source, radius);

        while (it.hasNext()) {
            it.next();
        }

        return it.getPaths();
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink)
    {
        return new DijkstraShortestPath<>(graph).getPath(source, sink);
    }

}


package org.jgrapht.alg.shortestpath;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;

public class KShortestPaths<V, E>
    implements KShortestPathAlgorithm<V, E>
{
    /**
     * Graph on which shortest paths are searched.
     */
    private Graph<V, E> graph;

    private int nMaxHops;

    private int nPaths;

    private PathValidator<V, E> pathValidator;

    public KShortestPaths(Graph<V, E> graph, int k)
    {
        this(graph, k, null);
    }

    public KShortestPaths(Graph<V, E> graph, int k, PathValidator<V, E> pathValidator)
    {
        this(graph, k, graph.vertexSet().size() - 1, pathValidator);
    }

    public KShortestPaths(Graph<V, E> graph, int k, int nMaxHops)
    {
        this(graph, k, nMaxHops, null);
    }

    public KShortestPaths(Graph<V, E> graph, int k, int nMaxHops, PathValidator<V, E> pathValidator)
    {
        this.graph = Objects.requireNonNull(graph, "graph is null");
        if (k <= 0) {
            throw new IllegalArgumentException("Number of paths must be positive");
        }
        this.nMaxHops = nMaxHops;
        if (nMaxHops <= 0) {
            throw new IllegalArgumentException("Max number of hops must be positive");
        }
        this.nPaths = k;
        this.pathValidator = pathValidator;
    }

    @Override
    public List<GraphPath<V, E>> getPaths(V startVertex, V endVertex)
    {
        Objects.requireNonNull(startVertex, "Start vertex cannot be null");
        Objects.requireNonNull(endVertex, "End vertex cannot be null");
        if (endVertex.equals(startVertex)) {
            throw new IllegalArgumentException("The end vertex is the same as the start vertex!");
        }
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("Graph must contain the start vertex!");
        }
        if (!graph.containsVertex(endVertex)) {
            throw new IllegalArgumentException("Graph must contain the end vertex!");
        }

        KShortestPathsIterator<V, E> iter =
            new KShortestPathsIterator<>(graph, startVertex, endVertex, nPaths, pathValidator);

        // at the i-th pass the shortest paths with less (or equal) than i edges
        // are calculated.
        for (int passNumber = 1; (passNumber <= nMaxHops) && iter.hasNext(); passNumber++) {
            iter.next();
        }

        List<RankingPathElement<V, E>> list = iter.getPathElements(endVertex);

        if (list == null) {
            return Collections.emptyList();
        }

        List<GraphPath<V, E>> pathList = new ArrayList<>();
        for (RankingPathElement<V, E> element : list) {
            pathList.add(
                new GraphWalk<>(
                    graph, startVertex, element.getVertex(), null, element.createEdgeListPath(),
                    element.getWeight()));
        }

        return pathList;
    }

}

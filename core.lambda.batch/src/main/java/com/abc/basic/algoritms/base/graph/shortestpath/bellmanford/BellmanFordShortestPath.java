package com.abc.basic.algoritms.base.graph.shortestpath.bellmanford;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.GraphPath;
import com.abc.basic.algoritms.base.graph.GraphWalk;
import com.abc.basic.algoritms.base.graph.shortestpath.BaseShortestPathAlgorithm;

/**
 * 单源最短路径，两个顶点间最短距离
 *
 * 带有权重的单源最短路径，边可为负值
 *
 */
public class BellmanFordShortestPath<V, E>
        extends BaseShortestPathAlgorithm<V, E>
{
    private static final double DEFAULT_EPSILON = 0.000000001;

    /**
     * Start vertex.
     */
    protected V startVertex;

    /**
     * Maximum number of edges of the calculated paths.
     */
    protected int nMaxHops;

    /**
     * Tolerance when comparing floating point values.
     */
    protected double epsilon;


    public BellmanFordShortestPath(Graph<V, E> graph)
    {
        this(graph, graph.vertexSet().size() - 1);
    }


    public BellmanFordShortestPath(Graph<V, E> graph, int nMaxHops)
    {
        this(graph, nMaxHops, DEFAULT_EPSILON);
    }

    public BellmanFordShortestPath(Graph<V, E> graph, int nMaxHops, double epsilon)
    {
        super(graph);
        this.nMaxHops = nMaxHops;
        this.epsilon = epsilon;
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink)
    {
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        return getPaths(source).getPath(sink);
    }


    @Override
    public SingleSourcePaths<V, E> getPaths(V source)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }

        BellmanFordIterator<V, E> iter = new BellmanFordIterator<>(graph, source, epsilon);

        // at the i-th pass the shortest paths with less (or equal) than i edges
        // are calculated.
        for (int passNumber = 1; (passNumber <= nMaxHops) && iter.hasNext(); passNumber++) {
            iter.next();
        }

        return new PathElementSingleSourcePaths(iter);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink)
    {
        return new BellmanFordShortestPath<>(graph).getPath(source, sink);
    }

    // interface wrapper
    private class PathElementSingleSourcePaths
            implements SingleSourcePaths<V, E>
    {
        private BellmanFordIterator<V, E> it;

        PathElementSingleSourcePaths(BellmanFordIterator<V, E> it)
        {
            this.it = it;
        }

        @Override
        public Graph<V, E> getGraph()
        {
            return it.graph;
        }

        @Override
        public V getSourceVertex()
        {
            return it.startVertex;
        }

        @Override
        public double getWeight(V targetVertex)
        {
            if (targetVertex.equals(it.startVertex)) {
                return 0d;
            }

            BellmanFordPathElement<V, E> pathElement = it.getPathElement(targetVertex);

            if (pathElement == null) {
                return Double.POSITIVE_INFINITY;
            } else {
                return pathElement.getCost();
            }
        }

        @Override
        public GraphPath<V, E> getPath(V targetVertex)
        {
            if (targetVertex.equals(it.startVertex)) {
                return createEmptyPath(it.startVertex, targetVertex);
            }

            BellmanFordPathElement<V, E> pathElement = it.getPathElement(targetVertex);

            if (pathElement == null) {
                return createEmptyPath(it.startVertex, targetVertex);
            } else {
                return new GraphWalk<>(
                        graph, it.startVertex, targetVertex, null, pathElement.createEdgeListPath(),
                        pathElement.getCost());
            }
        }

    }

}

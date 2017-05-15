package com.abc.basic.algoritms.base.graph.shortestpath.floydwarshall;

import com.abc.basic.algoritms.base.graph.*;
import com.abc.basic.algoritms.base.graph.shortestpath.BaseShortestPathAlgorithm;
import com.abc.basic.algoritms.base.graph.util.TypeUtil;

import java.util.*;

/**
 * The Floyd-Warshall algorithm.
 *
 * 动态规划所有结点对的最短路径
 */
public class FloydWarshallShortestPaths<V, E>
        extends BaseShortestPathAlgorithm<V, E>
{
    private final List<V> vertices;
    private final Map<V, Integer> vertexIndices;

    private int nShortestPaths = 0;
    private double diameter = Double.NaN;
    private double[][] d = null;
    private Object[][] backtrace = null;
    private Object[][] lastHopMatrix = null;

    public FloydWarshallShortestPaths(Graph<V, E> graph)
    {
        super(graph);
        this.vertices = new ArrayList<>(graph.vertexSet());
        this.vertexIndices = new HashMap<>(this.vertices.size());
        int i = 0;
        for (V vertex : vertices) {
            vertexIndices.put(vertex, i++);
        }
    }

    public int getShortestPathsCount()
    {
        lazyCalculateMatrix();

        return nShortestPaths;
    }

    public double getDiameter()
    {
        lazyCalculateMatrix();

        if (!Double.isNaN(diameter)) {
            return diameter;
        }

        int n = vertices.size();
        if (n > 0) {
            diameter = 0.0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    diameter = Double.max(diameter, d[i][j]);
                }
            }
        }
        return diameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V a, V b)
    {
        if (!graph.containsVertex(a)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }
        if (!graph.containsVertex(b)) {
            throw new IllegalArgumentException("graph must contain the sink vertex");
        }

        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == null) { // No path exists
            return createEmptyPath(a, b);
        }

        // Reconstruct the path
        List<E> edges = new ArrayList<>();
        V u = a;
        while (!u.equals(b)) {
            int v_u = vertexIndices.get(u);
            E e = TypeUtil.uncheckedCast(backtrace[v_u][v_b], null);
            edges.add(e);
            u = Graphs.getOppositeVertex(graph, e, u);
        }
        return new GraphWalk<>(graph, a, b, null, edges, d[v_a][v_b]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getPathWeight(V source, V sink)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("graph must contain the sink vertex");
        }

        lazyCalculateMatrix();

        return d[vertexIndices.get(source)][vertexIndices.get(sink)];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSourcePaths<V, E> getPaths(V source)
    {
        return new FloydWarshallSingleSourcePaths(source);
    }

    public V getFirstHop(V a, V b)
    {
        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == null) { // No path exists
            return null;
        } else {
            E e = TypeUtil.uncheckedCast(backtrace[v_a][v_b], null);
            return Graphs.getOppositeVertex(graph, e, a);
        }
    }

    public V getLastHop(V a, V b)
    {
        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == null) { // No path exists
            return null;
        } else {
            populateLastHopMatrix();
            E e = TypeUtil.uncheckedCast(lastHopMatrix[v_a][v_b], null);
            return Graphs.getOppositeVertex(graph, e, b);
        }
    }

    private void lazyCalculateMatrix()
    {
        if (d != null) {
            // already done
            return;
        }

        int n = vertices.size();

        // init the backtrace matrix
        backtrace = new Object[n][n];

        // initialize matrix, 0
        d = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(d[i], Double.POSITIVE_INFINITY);
        }

        // initialize matrix, 1
        for (int i = 0; i < n; i++) {
            d[i][i] = 0.0;
        }

        // initialize matrix, 2
        if (graph instanceof UndirectedGraph<?, ?>) {
            for (E edge : graph.edgeSet()) {
                V source = graph.getEdgeSource(edge);
                V target = graph.getEdgeTarget(edge);
                if (!source.equals(target)) {
                    int v_1 = vertexIndices.get(source);
                    int v_2 = vertexIndices.get(target);
                    double edgeWeight = graph.getEdgeWeight(edge);
                    if (Double.compare(edgeWeight, d[v_1][v_2]) < 0) {
                        d[v_1][v_2] = d[v_2][v_1] = edgeWeight;
                        backtrace[v_1][v_2] = edge;
                        backtrace[v_2][v_1] = edge;
                    }
                }
            }
        } else { // This works for both Directed and Mixed graphs! Iterating over
            // the arcs and querying source/sink does not suffice for graphs
            // which contain both edges and arcs
            DirectedGraph<V, E> directedGraph = (DirectedGraph<V, E>) graph;
            for (V v1 : directedGraph.vertexSet()) {
                int v_1 = vertexIndices.get(v1);
                for (E e : directedGraph.outgoingEdgesOf(v1)) {
                    V v2 = Graphs.getOppositeVertex(directedGraph, e, v1);
                    if (!v1.equals(v2)) {
                        int v_2 = vertexIndices.get(v2);
                        double edgeWeight = graph.getEdgeWeight(e);
                        if (Double.compare(edgeWeight, d[v_1][v_2]) < 0) {
                            d[v_1][v_2] = edgeWeight;
                            backtrace[v_1][v_2] = e;
                        }
                    }
                }
            }
        }

        // run fw alg
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double ik_kj = d[i][k] + d[k][j];
                    if (Double.compare(ik_kj, d[i][j]) < 0) {
                        d[i][j] = ik_kj;
                        backtrace[i][j] = backtrace[i][k];
                    }
                }
            }
        }

        // count shortest paths
        nShortestPaths = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && Double.isFinite(d[i][j])) {
                    nShortestPaths++;
                }
            }
        }
    }

    private void populateLastHopMatrix()
    {
        lazyCalculateMatrix();

        if (lastHopMatrix != null)
            return;

        // Initialize matrix
        int n = vertices.size();
        lastHopMatrix = new Object[n][n];

        // Populate matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j || lastHopMatrix[i][j] != null || backtrace[i][j] == null)
                    continue;

                // Reconstruct the path from i to j
                List<E> edges = new ArrayList<>();
                V u = vertices.get(i);
                V b = vertices.get(j);
                while (!u.equals(b)) {
                    int v_u = vertexIndices.get(u);
                    E e = TypeUtil.uncheckedCast(backtrace[v_u][j], null);
                    edges.add(e);
                    V other = Graphs.getOppositeVertex(graph, e, u);
                    lastHopMatrix[i][vertexIndices.get(other)] = e;
                    u = other;
                }
            }
        }
    }

    class FloydWarshallSingleSourcePaths
            implements SingleSourcePaths<V, E>
    {
        private V source;

        public FloydWarshallSingleSourcePaths(V source)
        {
            this.source = source;
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
        public double getWeight(V sink)
        {
            if (!graph.containsVertex(source)) {
                throw new IllegalArgumentException("graph must contain the source vertex");
            }
            if (!graph.containsVertex(sink)) {
                throw new IllegalArgumentException("graph must contain the sink vertex");
            }

            lazyCalculateMatrix();

            return d[vertexIndices.get(source)][vertexIndices.get(sink)];
        }

        @Override
        public GraphPath<V, E> getPath(V sink)
        {
            if (!graph.containsVertex(source)) {
                throw new IllegalArgumentException("graph must contain the source vertex");
            }
            if (!graph.containsVertex(sink)) {
                throw new IllegalArgumentException("graph must contain the sink vertex");
            }

            lazyCalculateMatrix();

            int v_a = vertexIndices.get(source);
            int v_b = vertexIndices.get(sink);

            if (backtrace[v_a][v_b] == null) { // No path exists
                return createEmptyPath(source, sink);
            }

            // Reconstruct the path
            List<E> edges = new ArrayList<>();
            V u = source;
            while (!u.equals(sink)) {
                int v_u = vertexIndices.get(u);
                E e = TypeUtil.uncheckedCast(backtrace[v_u][v_b], null);
                edges.add(e);
                u = Graphs.getOppositeVertex(graph, e, u);
            }
            return new GraphWalk<>(graph, source, sink, null, edges, d[v_a][v_b]);
        }

    }

}

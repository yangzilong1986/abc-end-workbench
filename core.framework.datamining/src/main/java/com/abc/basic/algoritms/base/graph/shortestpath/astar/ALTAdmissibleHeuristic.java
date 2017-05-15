package com.abc.basic.algoritms.base.graph.shortestpath.astar;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.EdgeReversedGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.UndirectedGraph;
import com.abc.basic.algoritms.base.graph.shortestpath.ShortestPathAlgorithm;
import com.abc.basic.algoritms.base.graph.shortestpath.dijkstra.DijkstraShortestPath;
import com.abc.basic.algoritms.base.graph.util.ToleranceDoubleComparator;

import java.util.*;

public class ALTAdmissibleHeuristic<V, E>
        implements AStarAdmissibleHeuristic<V>
{
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;
    private final Map<V, Map<V, Double>> fromLandmark;
    private final Map<V, Map<V, Double>> toLandmark;
    private final boolean directed;

    /**
     * Constructs a new {@link AStarAdmissibleHeuristic} using a set of landmarks.
     *
     * @param graph the graph
     * @param landmarks a set of vertices of the graph which will be used as landmarks
     *
     * @throws IllegalArgumentException if no landmarks are provided
     * @throws IllegalArgumentException if the graph contains edges with negative weights
     */
    public ALTAdmissibleHeuristic(Graph<V, E> graph, Set<V> landmarks)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(landmarks, "Landmarks cannot be null");
        if (landmarks.isEmpty()) {
            throw new IllegalArgumentException("At least one landmark must be provided");
        }
        this.fromLandmark = new HashMap<>();
        if (graph instanceof DirectedGraph) {
            this.directed = true;
            this.toLandmark = new HashMap<>();
        } else if (graph instanceof UndirectedGraph) {
            this.directed = false;
            this.toLandmark = this.fromLandmark;
        } else {
            throw new IllegalArgumentException("Graph must be directed or undirected");
        }
        this.comparator = new ToleranceDoubleComparator();

        // precomputation and validation
        for (V v : landmarks) {
            for (E e : graph.edgesOf(v)) {
                if (comparator.compare(graph.getEdgeWeight(e), 0d) < 0) {
                    throw new IllegalArgumentException("Graph edge weights cannot be negative");
                }
            }
            precomputeToFromLandmark(v);
        }
    }

    /**
     * An admissible heuristic estimate from a source vertex to a target vertex. The estimate is
     * always non-negative and never overestimates the true distance.
     *
     * @param u the source vertex
     * @param t the target vertex
     *
     * @return an admissible heuristic estimate
     */
    @Override
    public double getCostEstimate(V u, V t)
    {
        double maxEstimate = 0d;

        /*
         * Special case, source equals target
         */
        if (u.equals(t)) {
            return maxEstimate;
        }

        /*
         * Special case, source is landmark
         */
        if (fromLandmark.containsKey(u)) {
            return fromLandmark.get(u).get(t);
        }

        /*
         * Special case, target is landmark
         */
        if (toLandmark.containsKey(t)) {
            return toLandmark.get(t).get(u);
        }

        /*
         * Compute from landmarks
         */
        for (V l : fromLandmark.keySet()) {
            double estimate;
            Map<V, Double> from = fromLandmark.get(l);
            if (directed) {
                Map<V, Double> to = toLandmark.get(l);
                estimate = Math.max(to.get(u) - to.get(t), from.get(t) - from.get(u));
            } else {
                estimate = Math.abs(from.get(u) - from.get(t));
            }

            // max over all landmarks
            if (Double.isFinite(estimate)) {
                maxEstimate = Math.max(maxEstimate, estimate);
            }
        }

        return maxEstimate;
    }

    /**
     * Compute all distances to and from a landmark
     *
     * @param landmark the landmark
     */
    private void precomputeToFromLandmark(V landmark)
    {
        // compute distances from landmark
        ShortestPathAlgorithm.SingleSourcePaths<V, E> fromLandmarkPaths =
                new DijkstraShortestPath<>(graph).getPaths(landmark);
        Map<V, Double> fromLandMarkDistances = new HashMap<>();
        for (V v : graph.vertexSet()) {
            fromLandMarkDistances.put(v, fromLandmarkPaths.getWeight(v));
        }
        fromLandmark.put(landmark, fromLandMarkDistances);

        // compute distances to landmark (using reverse graph)
        if (directed) {
            DirectedGraph<V, E> reverseGraph = new EdgeReversedGraph<>((DirectedGraph<V, E>) graph);
            ShortestPathAlgorithm.SingleSourcePaths<V, E> toLandmarkPaths =
                    new DijkstraShortestPath<>(reverseGraph).getPaths(landmark);
            Map<V, Double> toLandMarkDistances = new HashMap<>();
            for (V v : graph.vertexSet()) {
                toLandMarkDistances.put(v, toLandmarkPaths.getWeight(v));
            }
            toLandmark.put(landmark, toLandMarkDistances);
        }
    }

}

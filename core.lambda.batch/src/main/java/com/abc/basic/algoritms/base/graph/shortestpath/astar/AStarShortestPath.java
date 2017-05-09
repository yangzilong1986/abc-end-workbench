package com.abc.basic.algoritms.base.graph.shortestpath.astar;

import com.abc.basic.algoritms.base.graph.*;
import com.abc.basic.algoritms.base.graph.shortestpath.BaseShortestPathAlgorithm;
import com.abc.basic.algoritms.base.graph.util.FibonacciHeap;
import com.abc.basic.algoritms.base.graph.util.FibonacciHeapNode;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

import java.util.*;

public class AStarShortestPath<V, E>
        extends BaseShortestPathAlgorithm<V, E>
{
    // List of open nodes
    protected FibonacciHeap<V> openList;
    protected Map<V, FibonacciHeapNode<V>> vertexToHeapNodeMap;

    // List of closed nodes
    protected Set<V> closedList;

    // Mapping of nodes to their g-scores (g(x)).
    protected Map<V, Double> gScoreMap;

    // Predecessor map: mapping of a node to an edge that leads to its
    // predecessor on its shortest path towards the targetVertex
    protected Map<V, E> cameFrom;

    // Reference to the admissible heuristic
    protected AStarAdmissibleHeuristic<V> admissibleHeuristic;

    // Counter which keeps track of the number of expanded nodes
    protected int numberOfExpandedNodes;

    // Comparator for comparing doubles with tolerance
    protected Comparator<Double> comparator;

    public AStarShortestPath(Graph<V, E> graph, AStarAdmissibleHeuristic<V> admissibleHeuristic)
    {
        super(graph);
        this.admissibleHeuristic =
                Objects.requireNonNull(admissibleHeuristic, "Heuristic function cannot be null!");
        this.comparator = new ToleranceDoubleComparator();
    }

    private void initialize(AStarAdmissibleHeuristic<V> admissibleHeuristic)
    {
        this.admissibleHeuristic = admissibleHeuristic;
        openList = new FibonacciHeap<>();
        vertexToHeapNodeMap = new HashMap<>();
        closedList = new HashSet<>();
        gScoreMap = new HashMap<>();
        cameFrom = new HashMap<>();
        numberOfExpandedNodes = 0;
    }

    @Override
    public GraphPath<V, E> getPath(V sourceVertex, V targetVertex)
    {
        if (!graph.containsVertex(sourceVertex) || !graph.containsVertex(targetVertex)) {
            throw new IllegalArgumentException(
                    "Source or target vertex not contained in the graph!");
        }

        if (sourceVertex.equals(targetVertex)) {
            return createEmptyPath(sourceVertex, targetVertex);
        }

        this.initialize(admissibleHeuristic);
        gScoreMap.put(sourceVertex, 0.0);
        FibonacciHeapNode<V> heapNode = new FibonacciHeapNode<>(sourceVertex);
        openList.insert(heapNode, 0.0);
        vertexToHeapNodeMap.put(sourceVertex, heapNode);

        do {
            FibonacciHeapNode<V> currentNode = openList.removeMin();

            // Check whether we reached the target vertex
            if (currentNode.getData().equals(targetVertex)) {
                // Build the path
                return this.buildGraphPath(sourceVertex, targetVertex, currentNode.getKey());
            }

            // We haven't reached the target vertex yet; expand the node
            expandNode(currentNode, targetVertex);
            closedList.add(currentNode.getData());
        } while (!openList.isEmpty());

        // No path exists from sourceVertex to TargetVertex
        return createEmptyPath(sourceVertex, targetVertex);
    }

    public int getNumberOfExpandedNodes()
    {
        return numberOfExpandedNodes;
    }

    public boolean isConsistentHeuristic(AStarAdmissibleHeuristic<V> admissibleHeuristic)
    {
        for (V targetVertex : graph.vertexSet()) {
            for (E e : graph.edgeSet()) {
                double weight = graph.getEdgeWeight(e);
                V edgeSource = graph.getEdgeSource(e);
                V edgeTarget = graph.getEdgeTarget(e);
                double h_x = admissibleHeuristic.getCostEstimate(edgeSource, targetVertex);
                double h_y = admissibleHeuristic.getCostEstimate(edgeTarget, targetVertex);
                if (h_x > weight + h_y)
                    return false;
            }
        }
        return true;
    }

    private void expandNode(FibonacciHeapNode<V> currentNode, V endVertex)
    {
        numberOfExpandedNodes++;

        Set<E> outgoingEdges = null;
        if (graph instanceof UndirectedGraph) {
            outgoingEdges = graph.edgesOf(currentNode.getData());
        } else if (graph instanceof DirectedGraph) {
            outgoingEdges = ((DirectedGraph<V, E>) graph).outgoingEdgesOf(currentNode.getData());
        }

        for (E edge : outgoingEdges) {
            V successor = Graphs.getOppositeVertex(graph, edge, currentNode.getData());

            if (successor.equals(currentNode.getData())) { // Ignore self-loop
                continue;
            }

            double gScore_current = gScoreMap.get(currentNode.getData());
            double tentativeGScore = gScore_current + graph.getEdgeWeight(edge);
            double fScore =
                    tentativeGScore + admissibleHeuristic.getCostEstimate(successor, endVertex);

            if (vertexToHeapNodeMap.containsKey(successor)) { // We re-encountered a vertex. It's
                // either in the open or closed list.
                if (tentativeGScore >= gScoreMap.get(successor)) // Ignore path since it is
                    // non-improving
                    continue;

                cameFrom.put(successor, edge);
                gScoreMap.put(successor, tentativeGScore);

                if (closedList.contains(successor)) { // it's in the closed list. Move node back to
                    // open list, since we discovered a shorter
                    // path to this node
                    closedList.remove(successor);
                    openList.insert(vertexToHeapNodeMap.get(successor), fScore);
                } else { // It's in the open list
                    openList.decreaseKey(vertexToHeapNodeMap.get(successor), fScore);
                }
            } else { // We've encountered a new vertex.
                cameFrom.put(successor, edge);
                gScoreMap.put(successor, tentativeGScore);
                FibonacciHeapNode<V> heapNode = new FibonacciHeapNode<>(successor);
                openList.insert(heapNode, fScore);
                vertexToHeapNodeMap.put(successor, heapNode);
            }
        }
    }

    private GraphPath<V, E> buildGraphPath(V startVertex, V targetVertex, double pathLength)
    {
        List<E> edgeList = new ArrayList<>();
        List<V> vertexList = new ArrayList<>();
        vertexList.add(targetVertex);

        V v = targetVertex;
        while (!v.equals(startVertex)) {
            edgeList.add(cameFrom.get(v));
            v = Graphs.getOppositeVertex(graph, cameFrom.get(v), v);
            vertexList.add(v);
        }
        Collections.reverse(edgeList);
        Collections.reverse(vertexList);
        return new GraphWalk<>(graph, startVertex, targetVertex, vertexList, edgeList, pathLength);
    }

}


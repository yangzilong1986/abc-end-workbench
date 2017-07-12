package com.abc.basic.algoritms.base.graph.shortestpath.astar;

public interface AStarAdmissibleHeuristic<V> {
    double getCostEstimate(V sourceVertex, V targetVertex);
}


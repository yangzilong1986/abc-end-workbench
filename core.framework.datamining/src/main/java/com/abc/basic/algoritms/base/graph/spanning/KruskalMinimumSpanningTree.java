package com.abc.basic.algoritms.base.graph.spanning;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.util.UnionFind;

import java.util.*;

/**
 * 最小生成树
 * 并集计算方法，即从图，总是找找到最小的边，加入集合
 */
public class KruskalMinimumSpanningTree<V, E>
        implements SpanningTreeAlgorithm<E>
{
    private final Graph<V, E> graph;

    public KruskalMinimumSpanningTree(Graph<V, E> graph)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    @Override
    public SpanningTree<E> getSpanningTree()
    {
        UnionFind<V> forest = new UnionFind<>(graph.vertexSet());
        ArrayList<E> allEdges = new ArrayList<>(graph.edgeSet());
        Collections.sort(
                allEdges, (edge1, edge2) -> Double
                        .valueOf(graph.getEdgeWeight(edge1)).compareTo(graph.getEdgeWeight(edge2)));

        double spanningTreeCost = 0;
        Set<E> edgeList = new HashSet<>();

        for (E edge : allEdges) {
            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);
            if (forest.find(source).equals(forest.find(target))) {
                continue;
            }

            forest.union(source, target);
            edgeList.add(edge);
            spanningTreeCost += graph.getEdgeWeight(edge);
        }

        return new SpanningTreeImpl<>(edgeList, spanningTreeCost);
    }
}


package org.jgrapht.alg.spanning;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.util.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Kruskal's_algorithm">Kruskal's minimum
 * spanning tree algorithm</a>. If the given graph is connected it computes the minimum spanning
 * tree, otherwise it computes the minimum spanning forest. The algorithm runs in time O(E log E).
 * This implementation uses the hashCode and equals method of the vertices.
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

// End KruskalMinimumSpanningTree.java

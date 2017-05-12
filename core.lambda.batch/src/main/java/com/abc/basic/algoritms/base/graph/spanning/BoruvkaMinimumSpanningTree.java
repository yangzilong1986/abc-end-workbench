package com.abc.basic.algoritms.base.graph.spanning;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.util.ToleranceDoubleComparator;
import com.abc.basic.algoritms.base.graph.util.UnionFind;

import java.util.*;

/**
 * 归并集实现
 * 该算法类似于KruskalMinimumSpanningTree，只是分阶段地向一组森林中逐渐添加边来构造一颗最小生成树
 * 每个阶段中，找出所有连接两颗不同的树的权重最小的边，并将它们全部添加入最小生成树。
 * 为了避免出现环，假设所有边的权重不同。
 *
 * 维护一个由顶点索引的数组来辨别连接每棵树和它最近的邻近的边。使用union-find结构
 * @param <V>
 * @param <E>
 */
public class BoruvkaMinimumSpanningTree<V, E>
        implements SpanningTreeAlgorithm<E>
{
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;


    public BoruvkaMinimumSpanningTree(Graph<V, E> graph)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.comparator = new ToleranceDoubleComparator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpanningTree<E> getSpanningTree()
    {
        // create result placeholder
        Set<E> mstEdges = new LinkedHashSet<>();
        double mstWeight = 0d;

        // fix edge order for unique comparison of edge weights
        Map<E, Integer> edgeOrder = new HashMap<>();
        int i = 0;
        for (E e : graph.edgeSet()) {
            edgeOrder.put(e, i++);
        }

        // initialize forest
        UnionFind<V> forest = new UnionFind<>(graph.vertexSet());
        Map<V, E> bestEdge = new LinkedHashMap<>();

        do {
            // find safe edges
            bestEdge.clear();
            for (E e : graph.edgeSet()) {
                V sTree = forest.find(graph.getEdgeSource(e));
                V tTree = forest.find(graph.getEdgeTarget(e));

                if (sTree.equals(tTree)) {
                    // same tree, skip
                    continue;
                }

                double eWeight = graph.getEdgeWeight(e);

                // check if better edge
                E sTreeEdge = bestEdge.get(sTree);
                if (sTreeEdge == null) {
                    bestEdge.put(sTree, e);
                } else {
                    double sTreeEdgeWeight = graph.getEdgeWeight(sTreeEdge);
                    int c = comparator.compare(eWeight, sTreeEdgeWeight);
                    if (c < 0 || (c == 0 && edgeOrder.get(e) < edgeOrder.get(sTreeEdge))) {
                        bestEdge.put(sTree, e);
                    }
                }

                // check if better edge
                E tTreeEdge = bestEdge.get(tTree);
                if (tTreeEdge == null) {
                    bestEdge.put(tTree, e);
                } else {
                    double tTreeEdgeWeight = graph.getEdgeWeight(tTreeEdge);
                    int c = comparator.compare(eWeight, tTreeEdgeWeight);
                    if (c < 0 || (c == 0 && edgeOrder.get(e) < edgeOrder.get(tTreeEdge))) {
                        bestEdge.put(tTree, e);
                    }
                }
            }

            // add safe edges to forest
            for (V v : bestEdge.keySet()) {
                E e = bestEdge.get(v);

                V sTree = forest.find(graph.getEdgeSource(e));
                V tTree = forest.find(graph.getEdgeTarget(e));

                if (sTree.equals(tTree)) {
                    // same tree, skip
                    continue;
                }

                mstEdges.add(e);
                mstWeight += graph.getEdgeWeight(e);

                forest.union(sTree, tTree);
            }
        } while (!bestEdge.isEmpty());

        // return mst
        return new SpanningTreeImpl<>(mstEdges, mstWeight);
    }
}

package com.abc.basic.algoritms.base.graph.strongconnectivity;


import com.abc.basic.algoritms.base.graph.Graphs;
import com.abc.basic.algoritms.base.graph.SimpleGraph;
import com.abc.basic.algoritms.base.graph.UndirectedGraph;
import com.abc.basic.algoritms.base.graph.events.ConnectivityInspector;

import java.util.*;

/**
 * 团最小
 * CLIQUE（Clustering In QUEst)是一种简单的基于网格的聚类方法，用于发现子空间中基于密度的簇。
 * CLIQUE把每个维划分成不重叠的区间，从而把数据对象的整个嵌入空间划分成单元。
 * 它使用一个密度阈值识别稠密单元和稀疏单元。一个单元是稠密的，如果映射到它的对象数超过该密度阈值。

 CLIQUE识别候选搜索空间的主要策略是使用稠密单元关于维度的单调性。这基于频繁模式和关联规则挖掘使用的先验性质。在子空间聚类的背景下，单调性陈述如下：

 一个k-维（>1）单元c至少有I个点，仅当c的每个（k-1）-维投影（它是（k-1）-维单元）至少有1个点。
 考虑下图，其中嵌人数据空间包含3个维：age,salary,vacation. 例如，子空间age和salary中的一个二维单元包含l个点，
 仅当该单元在每个维（即分别在age和salary上的投影都至少包含l个点).

 CLIQUE通过两个阶段进行聚类。在第一阶段，CLIQUE把d-维数据空间划分若干互不重叠的矩形单元，并且从中识别出稠密单元。
 CLIQUE在所有的子空间中发现稠密单元。为了做到这一点，CLIQUE把每个维都划分成区间，并识别至少包含l个点的区间，其中l是密度阈值。
 然后，CLIQUE迭代地连接子空间.CLIQUE检查中的点数是否满足密度阈值。当没有候选产生或候选都不稠密时，迭代终止。
 在第二阶段中，CLIQUE使用每个子空间中的稠密单元来装配可能具有任意形状的簇。其思想是利用最小描述长度（MDL)原理,使用最大区域来覆盖连接的稠密单元，
 其中最大区域是一个超矩形，落人该区域中的每个单元都是稠密的，并且该区域在该子空间的任何维上都不能再扩展。
 一般地找出簇的最佳描述是NP一困难的。因此，CLIQUE采用了一种简单的贪心方法。它从一个任意稠密单元开始，找出覆盖该单元的最大区域，
 然后在尚未被覆盖的剩余的稠密单元上继续这一过程。当所有稠密单元都被覆盖时，贪心方法终止。

 * @param <V>
 * @param <E>
 */
public class CliqueMinimalSeparatorDecomposition<V, E>
{
    /**
     * Source graph to operate on
     */
    private UndirectedGraph<V, E> graph;

    /**
     * Minimal triangulation of graph
     */
    private UndirectedGraph<V, E> chordalGraph;

    /**
     * Fill edges
     */
    private Set<E> fillEdges;

    /**
     * Minimal elimination ordering on the vertices of graph
     */
    private LinkedList<V> meo;

    /**
     * List of all vertices that generate a minimal separator of <code>
     * chordGraph</code>
     */
    private List<V> generators;

    /**
     * Set of clique minimal separators
     */
    private Set<Set<V>> separators;

    /**
     * The atoms generated by the decomposition
     */
    private Set<Set<V>> atoms;

    /**
     * Map for each separator how many components it produces.
     */
    private Map<Set<V>, Integer> fullComponentCount = new HashMap<>();

    /**
     * Setup a clique minimal separator decomposition on undirected graph <code>
     * g</code>. Loops and multiple edges are removed, i.e. the graph is transformed to a simple
     * graph.
     *
     * @param g The graph to decompose.
     */
    public CliqueMinimalSeparatorDecomposition(UndirectedGraph<V, E> g)
    {
        this.graph = g;
        this.fillEdges = new HashSet<>();
    }

    private void computeMinimalTriangulation()
    {
        // initialize chordGraph with same vertices as graph
        chordalGraph = new SimpleGraph<>(graph.getEdgeFactory());
        for (V v : graph.vertexSet()) {
            chordalGraph.addVertex(v);
        }

        // initialize g' as subgraph of graph (same vertices and edges)
        final UndirectedGraph<V, E> gprime = copyAsSimpleGraph(graph);
        int s = -1;
        generators = new ArrayList<>();
        meo = new LinkedList<>();

        final Map<V, Integer> vertexLabels = new HashMap<>();
        for (V v : gprime.vertexSet()) {
            vertexLabels.put(v, 0);
        }
        for (int i = 1, n = graph.vertexSet().size(); i <= n; i++) {
            V v = getMaxLabelVertex(vertexLabels);
            LinkedList<V> Y = new LinkedList<>(Graphs.neighborListOf(gprime, v));

            if (vertexLabels.get(v) <= s) {
                generators.add(v);
            }

            s = vertexLabels.get(v);

            // Mark x reached and all other vertices of gprime unreached
            HashSet<V> reached = new HashSet<>();
            reached.add(v);

            // mark neighborhood of x reached and add to reach(label(y))
            HashMap<Integer, HashSet<V>> reach = new HashMap<>();

            // mark y reached and add y to reach
            for (V y : Y) {
                reached.add(y);
                addToReach(vertexLabels.get(y), y, reach);
            }

            for (int j = 0; j < graph.vertexSet().size(); j++) {
                if (!reach.containsKey(j)) {
                    continue;
                }
                while (reach.get(j).size() > 0) {
                    // remove a vertex y from reach(j)
                    V y = reach.get(j).iterator().next();
                    reach.get(j).remove(y);

                    for (V z : Graphs.neighborListOf(gprime, y)) {
                        if (!reached.contains(z)) {
                            reached.add(z);
                            if (vertexLabels.get(z) > j) {
                                Y.add(z);
                                E fillEdge = graph.getEdgeFactory().createEdge(v, z);
                                fillEdges.add(fillEdge);
                                addToReach(vertexLabels.get(z), z, reach);
                            } else {
                                addToReach(j, z, reach);
                            }
                        }
                    }
                }
            }

            for (V y : Y) {
                chordalGraph.addEdge(v, y);
                vertexLabels.put(y, vertexLabels.get(y) + 1);
            }

            meo.addLast(v);
            gprime.removeVertex(v);
            vertexLabels.remove(v);
        }
    }

    /**
     * Get the vertex with the maximal label.
     *
     * @param vertexLabels Map that gives a label for each vertex.
     *
     * @return Vertex with the maximal label.
     */
    private V getMaxLabelVertex(Map<V, Integer> vertexLabels)
    {
        Iterator<Map.Entry<V, Integer>> iterator = vertexLabels.entrySet().iterator();
        Map.Entry<V, Integer> max = iterator.next();
        while (iterator.hasNext()) {
            Map.Entry<V, Integer> e = iterator.next();
            if (e.getValue() > max.getValue()) {
                max = e;
            }
        }
        return max.getKey();
    }

    /**
     * Add a vertex to reach.
     *
     * @param k vertex' label
     * @param v the vertex
     * @param r the reach structure.
     */
    private void addToReach(Integer k, V v, HashMap<Integer, HashSet<V>> r)
    {
        if (r.containsKey(k)) {
            r.get(k).add(v);
        } else {
            HashSet<V> set = new HashSet<>();
            set.add(v);
            r.put(k, set);
        }
    }

    /**
     * Compute the unique decomposition of the input graph G (atoms of G). Implementation of
     * algorithm Atoms as described in Berry et al. (2010), DOI:10.3390/a3020197,
     */
    private void computeAtoms()
    {
        if (chordalGraph == null) {
            computeMinimalTriangulation();
        }

        separators = new HashSet<>();

        // initialize g' as subgraph of graph (same vertices and edges)
        UndirectedGraph<V, E> gprime = copyAsSimpleGraph(graph);

        // initialize h' as subgraph of chordalGraph (same vertices and edges)
        UndirectedGraph<V, E> hprime = copyAsSimpleGraph(chordalGraph);

        atoms = new HashSet<>();

        Iterator<V> iterator = meo.descendingIterator();
        while (iterator.hasNext()) {
            V v = iterator.next();
            if (generators.contains(v)) {
                Set<V> separator = new HashSet<>(Graphs.neighborListOf(hprime, v));

                if (isClique(graph, separator)) {
                    if (separator.size() > 0) {
                        if (separators.contains(separator)) {
                            fullComponentCount
                                    .put(separator, fullComponentCount.get(separator) + 1);
                        } else {
                            fullComponentCount.put(separator, 2);
                            separators.add(separator);
                        }
                    }
                    UndirectedGraph<V, E> tmpGraph = copyAsSimpleGraph(gprime);

                    tmpGraph.removeAllVertices(separator);
                    ConnectivityInspector<V, E> con = new ConnectivityInspector<>(tmpGraph);
                    if (con.isGraphConnected()) {
                        throw new RuntimeException("separator did not separate the graph");
                    }
                    for (Set<V> component : con.connectedSets()) {
                        if (component.contains(v)) {
                            gprime.removeAllVertices(component);
                            component.addAll(separator);
                            atoms.add(new HashSet<>(component));
                            assert (component.size() > 0);
                            break;
                        }
                    }
                }
            }

            hprime.removeVertex(v);
        }

        if (gprime.vertexSet().size() > 0) {
            atoms.add(new HashSet<>(gprime.vertexSet()));
        }
    }

    /**
     * Check whether the subgraph of <code>graph</code> induced by the given <code>vertices</code>
     * is complete, i.e. a clique.
     *
     * @param graph the graph.
     * @param vertices the vertices to induce the subgraph from.
     *
     * @return true if the induced subgraph is a clique.
     */
    private static <V, E> boolean isClique(UndirectedGraph<V, E> graph, Set<V> vertices)
    {
        for (V v1 : vertices) {
            for (V v2 : vertices) {
                if ((v1 != v2) && (graph.getEdge(v1, v2) == null)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static <V, E> UndirectedGraph<V, E> copyAsSimpleGraph(UndirectedGraph<V, E> graph)
    {
        UndirectedGraph<V, E> copy = new SimpleGraph<>(graph.getEdgeFactory());

        if (graph instanceof SimpleGraph) {
            Graphs.addGraph(copy, graph);
        } else {
            // project graph to SimpleGraph
            Graphs.addAllVertices(copy, graph.vertexSet());
            for (E e : graph.edgeSet()) {
                V v1 = graph.getEdgeSource(e);
                V v2 = graph.getEdgeTarget(e);
                if ((v1 != v2) && !copy.containsEdge(e)) {
                    copy.addEdge(v1, v2);
                }
            }
        }
        return copy;
    }

    public boolean isChordal()
    {
        if (chordalGraph == null) {
            computeMinimalTriangulation();
        }

        return (chordalGraph.edgeSet().size() == graph.edgeSet().size());
    }

    public Set<E> getFillEdges()
    {
        if (fillEdges == null) {
            computeMinimalTriangulation();
        }

        return fillEdges;
    }

    public UndirectedGraph<V, E> getMinimalTriangulation()
    {
        if (chordalGraph == null) {
            computeMinimalTriangulation();
        }

        return chordalGraph;
    }

    public List<V> getGenerators()
    {
        if (generators == null) {
            computeMinimalTriangulation();
        }

        return generators;
    }

    public LinkedList<V> getMeo()
    {
        if (meo == null) {
            computeMinimalTriangulation();
        }

        return meo;
    }

    public Map<Set<V>, Integer> getFullComponentCount()
    {
        if (fullComponentCount == null) {
            computeAtoms();
        }

        return fullComponentCount;
    }

    public Set<Set<V>> getAtoms()
    {
        if (atoms == null) {
            computeAtoms();
        }

        return atoms;
    }

    public Set<Set<V>> getSeparators()
    {
        if (separators == null) {
            computeAtoms();
        }

        return separators;
    }

    public UndirectedGraph<V, E> getGraph()
    {
        return graph;
    }
}


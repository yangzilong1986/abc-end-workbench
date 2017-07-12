package com.abc.basic.algoritms.base.graph.scc;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.graph.DirectedSubgraph;
import com.abc.basic.algoritms.base.graph.graph.EdgeReversedGraph;

import java.util.*;

/**
 * 计算无向图的连通分量只是使用深度优先算法搜索的一个简单应用
 * 对于有向图的连通计算强连通
 * @param <V>
 * @param <E>
 */
public class KosarajuStrongConnectivityInspector<V, E>
        implements StrongConnectivityAlgorithm<V, E>
{
    // the graph to compute the strongly connected sets for
    private final DirectedGraph<V, E> graph;

    // stores the vertices, ordered by their finishing time in first dfs
    private LinkedList<VertexData<V>> orderedVertices;

    // the result of the computation, cached for future calls
    private List<Set<V>> stronglyConnectedSets;

    // the result of the computation, cached for future calls
    private List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs;

    // maps vertices to their VertexData object
    private Map<V, VertexData<V>> vertexToVertexData;

    /**
     * The constructor of the StrongConnectivityAlgorithm class.
     *
     * @param directedGraph the graph to inspect
     *
     * @throws IllegalArgumentException if the input graph is null
     */
    public KosarajuStrongConnectivityInspector(DirectedGraph<V, E> directedGraph)
    {
        if (directedGraph == null) {
            throw new IllegalArgumentException("null not allowed for graph!");
        }

        graph = directedGraph;
        vertexToVertexData = null;
        orderedVertices = null;
        stronglyConnectedSets = null;
        stronglyConnectedSubgraphs = null;
    }

    /**
     * Returns the graph inspected by the StrongConnectivityAlgorithm.
     *
     * @return the graph inspected by this StrongConnectivityAlgorithm
     */
    public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    public boolean isStronglyConnected()
    {
        return stronglyConnectedSets().size() == 1;
    }

    public List<Set<V>> stronglyConnectedSets()
    {
        if (stronglyConnectedSets == null) {
            orderedVertices = new LinkedList<VertexData<V>>();
            stronglyConnectedSets = new Vector<Set<V>>();

            // create VertexData objects for all vertices, store them
            createVertexData();

            // perform the first round of DFS, result is an ordering
            // of the vertices by decreasing finishing time
            for (VertexData<V> data : vertexToVertexData.values()) {
                if (!data.isDiscovered()) {
                    dfsVisit(graph, data, null);
                }
            }

            // 'create' inverse graph (i.e. every edge is reversed)
            DirectedGraph<V, E> inverseGraph = new EdgeReversedGraph<V, E>(graph);

            // get ready for next dfs round
            resetVertexData();

            // second dfs round: vertices are considered in decreasing
            // finishing time order; every tree found is a strongly
            // connected set
            for (VertexData<V> data : orderedVertices) {
                if (!data.isDiscovered()) {
                    // new strongly connected set
                    Set<V> set = new HashSet<V>();
                    stronglyConnectedSets.add(set);
                    dfsVisit(inverseGraph, data, set);
                }
            }

            // clean up for garbage collection
            orderedVertices = null;
            vertexToVertexData = null;
        }

        return stronglyConnectedSets;
    }

    public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs()
    {
        if (stronglyConnectedSubgraphs == null) {
            List<Set<V>> sets = stronglyConnectedSets();
            stronglyConnectedSubgraphs = new Vector<DirectedSubgraph<V, E>>(sets.size());

            for (Set<V> set : sets) {
                stronglyConnectedSubgraphs.add(new DirectedSubgraph<V, E>(graph, set, null));
            }
        }

        return stronglyConnectedSubgraphs;
    }

    private void createVertexData()
    {
        vertexToVertexData = new HashMap<V, VertexData<V>>(graph.vertexSet().size());

        for (V vertex : graph.vertexSet()) {
            vertexToVertexData.put(vertex, new VertexData2<V>(vertex, false, false));
        }
    }

    private void dfsVisit(
            DirectedGraph<V, E> visitedGraph, VertexData<V> vertexData, Set<V> vertices)
    {
        Deque<VertexData<V>> stack = new ArrayDeque<VertexData<V>>();
        stack.add(vertexData);

        while (!stack.isEmpty()) {
            VertexData<V> data = stack.removeLast();

            if (!data.isDiscovered()) {
                data.setDiscovered(true);

                if (vertices != null) {
                    vertices.add(data.getVertex());
                }

                stack.add(new VertexData1<V>(data, true, true));

                // follow all edges
                for (E edge : visitedGraph.outgoingEdgesOf(data.getVertex())) {
                    VertexData<V> targetData =
                            vertexToVertexData.get(visitedGraph.getEdgeTarget(edge));

                    if (!targetData.isDiscovered()) {
                        // the "recursion"
                        stack.add(targetData);
                    }
                }
            } else if (data.isFinished()) {
                if (vertices == null) {
                    orderedVertices.addFirst(data.getFinishedData());
                }
            }
        }
    }

    private void resetVertexData()
    {
        for (VertexData<V> data : vertexToVertexData.values()) {
            data.setDiscovered(false);
            data.setFinished(false);
        }
    }

    private static abstract class VertexData<V>
    {
        private byte bitfield;

        private VertexData(boolean discovered, boolean finished)
        {
            this.bitfield = 0;
            setDiscovered(discovered);
            setFinished(finished);
        }

        private boolean isDiscovered()
        {
            return (bitfield & 1) == 1;
        }

        private boolean isFinished()
        {
            return (bitfield & 2) == 2;
        }

        private void setDiscovered(boolean discovered)
        {
            if (discovered) {
                bitfield |= 1;
            } else {
                bitfield &= ~1;
            }
        }

        private void setFinished(boolean finished)
        {
            if (finished) {
                bitfield |= 2;
            } else {
                bitfield &= ~2;
            }
        }

        abstract VertexData<V> getFinishedData();

        abstract V getVertex();
    }

    private static final class VertexData1<V>
            extends VertexData<V>
    {
        private final VertexData<V> finishedData;

        private VertexData1(VertexData<V> finishedData, boolean discovered, boolean finished)
        {
            super(discovered, finished);
            this.finishedData = finishedData;
        }

        @Override
        VertexData<V> getFinishedData()
        {
            return finishedData;
        }

        @Override
        V getVertex()
        {
            return null;
        }
    }

    private static final class VertexData2<V>
            extends VertexData<V>
    {
        private final V vertex;

        private VertexData2(V vertex, boolean discovered, boolean finished)
        {
            super(discovered, finished);
            this.vertex = vertex;
        }

        @Override
        VertexData<V> getFinishedData()
        {
            return null;
        }

        @Override
        V getVertex()
        {
            return vertex;
        }
    }
}

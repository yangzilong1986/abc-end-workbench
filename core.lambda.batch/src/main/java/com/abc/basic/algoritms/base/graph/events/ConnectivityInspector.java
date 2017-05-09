package com.abc.basic.algoritms.base.graph.events;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.UndirectedGraph;
import com.abc.basic.algoritms.base.graph.graph.AsUndirectedGraph;
import com.abc.basic.algoritms.base.graph.traverse.BreadthFirstIterator;

import java.util.*;

public class ConnectivityInspector<V, E>
        implements GraphListener<V, E>
{
    List<Set<V>> connectedSets;
    Map<V, Set<V>> vertexToConnectedSet;
    private Graph<V, E> graph;

    /**
     * Creates a connectivity inspector for the specified undirected graph.
     *
     * @param g the graph for which a connectivity inspector to be created.
     */
    public ConnectivityInspector(UndirectedGraph<V, E> g)
    {
        init();
        this.graph = g;
    }

    /**
     * Creates a connectivity inspector for the specified directed graph.
     *
     * @param g the graph for which a connectivity inspector to be created.
     */
    public ConnectivityInspector(DirectedGraph<V, E> g)
    {
        init();
        this.graph = new AsUndirectedGraph<>(g);
    }

    /**
     * Test if the inspected graph is connected. An empty graph is <i>not</i> considered connected.
     *
     * @return <code>true</code> if and only if inspected graph is connected.
     */
    public boolean isGraphConnected()
    {
        return lazyFindConnectedSets().size() == 1;
    }


    public Set<V> connectedSetOf(V vertex)
    {
        Set<V> connectedSet = vertexToConnectedSet.get(vertex);

        if (connectedSet == null) {
            connectedSet = new HashSet<>();

            BreadthFirstIterator<V, E> i = new BreadthFirstIterator<>(graph, vertex);

            while (i.hasNext()) {
                connectedSet.add(i.next());
            }

            vertexToConnectedSet.put(vertex, connectedSet);
        }

        return connectedSet;
    }

    public List<Set<V>> connectedSets()
    {
        return lazyFindConnectedSets();
    }

    /**
     * @see GraphListener#edgeAdded(GraphEdgeChangeEvent)
     */
    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e)
    {
        init(); // for now invalidate cached results, in the future need to
        // amend them.
    }

    /**
     * @see GraphListener#edgeRemoved(GraphEdgeChangeEvent)
     */
    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e)
    {
        init(); // for now invalidate cached results, in the future need to
        // amend them.
    }

    public boolean pathExists(V sourceVertex, V targetVertex)
    {
        /*
         * TODO: Ignoring edge direction for directed graph may be confusing. For directed graphs,
         * consider Dijkstra's algorithm.
         */
        Set<V> sourceSet = connectedSetOf(sourceVertex);

        return sourceSet.contains(targetVertex);
    }

    /**
     * @see VertexSetListener#vertexAdded(GraphVertexChangeEvent)
     */
    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> e)
    {
        init(); // for now invalidate cached results, in the future need to
        // amend them.
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> e)
    {
        init(); // for now invalidate cached results, in the future need to
        // amend them.
    }

    private void init()
    {
        connectedSets = null;
        vertexToConnectedSet = new HashMap<>();
    }

    private List<Set<V>> lazyFindConnectedSets()
    {
        if (connectedSets == null) {
            connectedSets = new ArrayList<>();

            Set<V> vertexSet = graph.vertexSet();

//            if (vertexSet.size() > 0) {
//                BreadthFirstIterator<V, E> i = new BreadthFirstIterator<>(graph, null);
//                i.addTraversalListener(new MyTraversalListener());
//
//                while (i.hasNext()) {
//                    i.next();
//                }
//            }
        }

        return connectedSets;
    }

    /**
     * A traversal listener that groups all vertices according to to their containing connected set.
     *
     * @author Barak Naveh
     * @since Aug 6, 2003
     */
//    private class MyTraversalListener
//            extends TraversalListenerAdapter<V, E>
//    {
//        private Set<V> currentConnectedSet;
//
//        /**
//         * @see TraversalListenerAdapter#connectedComponentFinished(ConnectedComponentTraversalEvent)
//         */
//        @Override
//        public void connectedComponentFinished(ConnectedComponentTraversalEvent e)
//        {
//            connectedSets.add(currentConnectedSet);
//        }
//
//        /**
//         * @see TraversalListenerAdapter#connectedComponentStarted(ConnectedComponentTraversalEvent)
//         */
//        @Override
//        public void connectedComponentStarted(ConnectedComponentTraversalEvent e)
//        {
//            currentConnectedSet = new HashSet<>();
//        }
//
//        /**
//         * @see TraversalListenerAdapter#vertexTraversed(VertexTraversalEvent)
//         */
//        @Override
//        public void vertexTraversed(VertexTraversalEvent<V> e)
//        {
//            V v = e.getVertex();
//            currentConnectedSet.add(v);
//            vertexToConnectedSet.put(v, currentConnectedSet);
//        }
//    }
}


package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.strongconnectivity.KosarajuStrongConnectivityInspector;
import com.abc.basic.algoritms.base.graph.strongconnectivity.StrongConnectivityAlgorithm;
import com.abc.basic.algoritms.base.graph.traverse.DepthFirstIterator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class CycleDetector<V, E>
{

    DirectedGraph<V, E> graph;

    public CycleDetector(DirectedGraph<V, E> graph)
    {
        this.graph = graph;
    }

    public boolean detectCycles()
    {
        try {
            execute(null, null);
        } catch (CycleDetectedException ex) {
            return true;
        }

        return false;
    }

    public boolean detectCyclesContainingVertex(V v)
    {
        try {
            execute(null, v);
        } catch (CycleDetectedException ex) {
            return true;
        }

        return false;
    }

    public Set<V> findCycles()
    {
        // ProbeIterator can't be used to handle this case,
        // so use StrongConnectivityAlgorithm instead.
        StrongConnectivityAlgorithm<V, E> inspector =
                new KosarajuStrongConnectivityInspector<>(graph);
        List<Set<V>> components = inspector.stronglyConnectedSets();

        // A vertex participates in a cycle if either of the following is
        // true: (a) it is in a component whose size is greater than 1
        // or (b) it is a self-loop

        Set<V> set = new LinkedHashSet<>();
        for (Set<V> component : components) {
            if (component.size() > 1) {
                // cycle
                set.addAll(component);
            } else {
                V v = component.iterator().next();
                if (graph.containsEdge(v, v)) {
                    // self-loop
                    set.add(v);
                }
            }
        }

        return set;
    }

    public Set<V> findCyclesContainingVertex(V v)
    {
        Set<V> set = new LinkedHashSet<>();
        execute(set, v);

        return set;
    }

    private void execute(Set<V> s, V v)
    {
        ProbeIterator iter = new ProbeIterator(s, v);

        while (iter.hasNext()) {
            iter.next();
        }
    }

    /**
     * Exception thrown internally when a cycle is detected during a yes/no cycle test. Must be
     * caught by top-level detection method.
     */
    private static class CycleDetectedException
            extends RuntimeException
    {
        private static final long serialVersionUID = 3834305137802950712L;
    }

    /**
     * Version of DFS which maintains a backtracking path used to probe for cycles.
     */
    private class ProbeIterator
            extends DepthFirstIterator<V, E>
    {
        private List<V> path;
        private Set<V> cycleSet;
        private V root;

        ProbeIterator(Set<V> cycleSet, V startVertex)
        {
            super(graph, startVertex);
            root = startVertex;
            this.cycleSet = cycleSet;
            path = new ArrayList<>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void encounterVertexAgain(V vertex, E edge)
        {
            super.encounterVertexAgain(vertex, edge);

            int i;

            if (root != null) {
                // For rooted detection, the path must either
                // double back to the root, or to a node of a cycle
                // which has already been detected.
                if (vertex.equals(root)) {
                    i = 0;
                } else if ((cycleSet != null) && cycleSet.contains(vertex)) {
                    i = 0;
                } else {
                    return;
                }
            } else {
                i = path.indexOf(vertex);
            }

            if (i > -1) {
                if (cycleSet == null) {
                    // we're doing yes/no cycle detection
                    throw new CycleDetectedException();
                } else {
                    for (; i < path.size(); ++i) {
                        cycleSet.add(path.get(i));
                    }
                }
            }
        }

        @Override
        protected V provideNextVertex()
        {
            V v = super.provideNextVertex();

            // backtrack
            for (int i = path.size() - 1; i >= 0; --i) {
                if (graph.containsEdge(path.get(i), v)) {
                    break;
                }

                path.remove(i);
            }

            path.add(v);

            return v;
        }
    }
}

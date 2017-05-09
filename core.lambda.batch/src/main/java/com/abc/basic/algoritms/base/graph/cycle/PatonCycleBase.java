package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.UndirectedGraph;

import java.util.*;

public class PatonCycleBase<V, E>
        implements UndirectedCycleBase<V, E>
{
    private UndirectedGraph<V, E> graph;

    /**
     * Create a cycle base finder with an unspecified graph.
     */
    public PatonCycleBase()
    {
    }

    public PatonCycleBase(UndirectedGraph<V, E> graph)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }


    @Override
    public UndirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGraph(UndirectedGraph<V, E> graph)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<V>> findCycleBase()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        Map<V, Set<V>> used = new HashMap<>();
        Map<V, V> parent = new HashMap<>();
        ArrayDeque<V> stack = new ArrayDeque<>();
        List<List<V>> cycles = new ArrayList<>();

        for (V root : graph.vertexSet()) {
            // Loop over the connected
            // components of the graph.
            if (parent.containsKey(root)) {
                continue;
            }

            // Free some memory in case of
            // multiple connected components.
            used.clear();

            // Prepare to walk the spanning tree.
            parent.put(root, root);
            used.put(root, new HashSet<>());
            stack.push(root);

            // Do the walk. It is a BFS with
            // a LIFO instead of the usual
            // FIFO. Thus it is easier to
            // find the cycles in the tree.
            while (!stack.isEmpty()) {
                V current = stack.pop();
                Set<V> currentUsed = used.get(current);
                for (E e : graph.edgesOf(current)) {
                    V neighbor = graph.getEdgeTarget(e);
                    if (neighbor.equals(current)) {
                        neighbor = graph.getEdgeSource(e);
                    }
                    if (!used.containsKey(neighbor)) {
                        // found a new node
                        parent.put(neighbor, current);
                        Set<V> neighbourUsed = new HashSet<>();
                        neighbourUsed.add(current);
                        used.put(neighbor, neighbourUsed);
                        stack.push(neighbor);
                    } else if (neighbor.equals(current)) {
                        // found a self loop
                        List<V> cycle = new ArrayList<>();
                        cycle.add(current);
                        cycles.add(cycle);
                    } else if (!currentUsed.contains(neighbor)) {
                        // found a cycle
                        Set<V> neighbourUsed = used.get(neighbor);
                        List<V> cycle = new ArrayList<>();
                        cycle.add(neighbor);
                        cycle.add(current);
                        V p = parent.get(current);
                        while (!neighbourUsed.contains(p)) {
                            cycle.add(p);
                            p = parent.get(p);
                        }
                        cycle.add(p);
                        cycles.add(cycle);
                        neighbourUsed.add(current);
                    }
                }
            }
        }
        return cycles;
    }
}


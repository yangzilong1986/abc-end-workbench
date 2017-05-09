package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.DirectedGraph;

import java.util.*;

public class TiernanSimpleCycles<V, E>
        implements DirectedSimpleCycles<V, E>
{
    private DirectedGraph<V, E> graph;

    public TiernanSimpleCycles()
    {
    }

    public TiernanSimpleCycles(DirectedGraph<V, E> graph)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }

    @Override
    public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    @Override
    public void setGraph(DirectedGraph<V, E> graph)
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
    public List<List<V>> findSimpleCycles()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        Map<V, Integer> indices = new HashMap<>();
        List<V> path = new ArrayList<>();
        Set<V> pathSet = new HashSet<>();
        Map<V, Set<V>> blocked = new HashMap<>();
        List<List<V>> cycles = new LinkedList<>();

        int index = 0;
        for (V v : graph.vertexSet()) {
            blocked.put(v, new HashSet<>());
            indices.put(v, index++);
        }

        Iterator<V> vertexIterator = graph.vertexSet().iterator();
        if (!vertexIterator.hasNext()) {
            return cycles;
        }

        V startOfPath;
        V endOfPath;
        V temp;
        int endIndex;
        boolean extensionFound;

        endOfPath = vertexIterator.next();
        path.add(endOfPath);
        pathSet.add(endOfPath);

        // A mostly straightforward implementation
        // of the algorithm. Except that there is
        // no real need for the state machine from
        // the original paper.
        while (true) {
            // path extension
            do {
                extensionFound = false;
                for (E e : graph.outgoingEdgesOf(endOfPath)) {
                    V n = graph.getEdgeTarget(e);
                    int cmp = indices.get(n).compareTo(indices.get(path.get(0)));
                    if ((cmp > 0) && !pathSet.contains(n) && !blocked.get(endOfPath).contains(n)) {
                        path.add(n);
                        pathSet.add(n);
                        endOfPath = n;
                        extensionFound = true;
                        break;
                    }
                }
            } while (extensionFound);

            // circuit confirmation
            startOfPath = path.get(0);
            if (graph.containsEdge(endOfPath, startOfPath)) {
                List<V> cycle = new ArrayList<>();
                cycle.addAll(path);
                cycles.add(cycle);
            }

            // vertex closure
            if (path.size() > 1) {
                blocked.get(endOfPath).clear();
                endIndex = path.size() - 1;
                path.remove(endIndex);
                pathSet.remove(endOfPath);
                --endIndex;
                temp = endOfPath;
                endOfPath = path.get(endIndex);
                blocked.get(endOfPath).add(temp);
                continue;
            }

            // advance initial index
            if (vertexIterator.hasNext()) {
                path.clear();
                pathSet.clear();
                endOfPath = vertexIterator.next();
                path.add(endOfPath);
                pathSet.add(endOfPath);
                for (V vt : blocked.keySet()) {
                    blocked.get(vt).clear();
                }
                continue;
            }

            // terminate
            break;
        }

        return cycles;
    }
}


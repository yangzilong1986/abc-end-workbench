package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.DirectedGraph;

import java.util.*;

public class TarjanSimpleCycles<V, E>
        implements DirectedSimpleCycles<V, E>
{
    private DirectedGraph<V, E> graph;

    private List<List<V>> cycles;
    private Set<V> marked;
    private ArrayDeque<V> markedStack;
    private ArrayDeque<V> pointStack;
    private Map<V, Integer> vToI;
    private Map<V, Set<V>> removed;

    public TarjanSimpleCycles()
    {
    }

    public TarjanSimpleCycles(DirectedGraph<V, E> graph)
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

    @Override
    public List<List<V>> findSimpleCycles()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        initState();

        for (V start : graph.vertexSet()) {
            backtrack(start, start);
            while (!markedStack.isEmpty()) {
                marked.remove(markedStack.pop());
            }
        }

        List<List<V>> result = cycles;
        clearState();
        return result;
    }

    private boolean backtrack(V start, V vertex)
    {
        boolean foundCycle = false;
        pointStack.push(vertex);
        marked.add(vertex);
        markedStack.push(vertex);

        for (E currentEdge : graph.outgoingEdgesOf(vertex)) {
            V currentVertex = graph.getEdgeTarget(currentEdge);
            if (getRemoved(vertex).contains(currentVertex)) {
                continue;
            }
            int comparison = toI(currentVertex).compareTo(toI(start));
            if (comparison < 0) {
                getRemoved(vertex).add(currentVertex);
            } else if (comparison == 0) {
                foundCycle = true;
                List<V> cycle = new ArrayList<>();
                Iterator<V> it = pointStack.descendingIterator();
                V v;
                while (it.hasNext()) {
                    v = it.next();
                    if (start.equals(v)) {
                        break;
                    }
                }
                cycle.add(start);
                while (it.hasNext()) {
                    cycle.add(it.next());
                }
                cycles.add(cycle);
            } else if (!marked.contains(currentVertex)) {
                boolean gotCycle = backtrack(start, currentVertex);
                foundCycle = foundCycle || gotCycle;
            }
        }

        if (foundCycle) {
            while (!markedStack.peek().equals(vertex)) {
                marked.remove(markedStack.pop());
            }
            marked.remove(markedStack.pop());
        }

        pointStack.pop();
        return foundCycle;
    }

    private void initState()
    {
        cycles = new ArrayList<>();
        marked = new HashSet<>();
        markedStack = new ArrayDeque<>();
        pointStack = new ArrayDeque<>();
        vToI = new HashMap<>();
        removed = new HashMap<>();
        int index = 0;
        for (V v : graph.vertexSet()) {
            vToI.put(v, index++);
        }
    }

    private void clearState()
    {
        cycles = null;
        marked = null;
        markedStack = null;
        pointStack = null;
        vToI = null;
    }

    private Integer toI(V v)
    {
        return vToI.get(v);
    }

    private Set<V> getRemoved(V v)
    {
        // Removed sets typically not all
        // needed, so instantiate lazily.
        Set<V> result = removed.get(v);
        if (result == null) {
            result = new HashSet<>();
            removed.put(v, result);
        }
        return result;
    }
}

package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.scc.KosarajuStrongConnectivityInspector;

import java.util.*;

public class SzwarcfiterLauerSimpleCycles<V, E>
        implements DirectedSimpleCycles<V, E>
{
    // The graph.
    private DirectedGraph<V, E> graph;

    // The state of the algorithm.
    private List<List<V>> cycles = null;
    private V[] iToV = null;
    private Map<V, Integer> vToI = null;
    private Map<V, Set<V>> bSets = null;
    private ArrayDeque<V> stack = null;
    private Set<V> marked = null;
    private Map<V, Set<V>> removed = null;
    private int[] position = null;
    private boolean[] reach = null;
    private List<V> startVertices = null;

    public SzwarcfiterLauerSimpleCycles()
    {
    }

    public SzwarcfiterLauerSimpleCycles(DirectedGraph<V, E> graph)
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
        // Just a straightforward implementation of
        // the algorithm.
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        initState();
        KosarajuStrongConnectivityInspector<V, E> inspector =
                new KosarajuStrongConnectivityInspector<>(graph);
        List<Set<V>> sccs = inspector.stronglyConnectedSets();
        for (Set<V> scc : sccs) {
            int maxInDegree = -1;
            V startVertex = null;
            for (V v : scc) {
                int inDegree = graph.inDegreeOf(v);
                if (inDegree > maxInDegree) {
                    maxInDegree = inDegree;
                    startVertex = v;
                }
            }
            startVertices.add(startVertex);
        }

        for (V vertex : startVertices) {
            cycle(toI(vertex), 0);
        }

        List<List<V>> result = cycles;
        clearState();
        return result;
    }

    private boolean cycle(int v, int q)
    {
        boolean foundCycle = false;
        V vV = toV(v);
        marked.add(vV);
        stack.push(vV);
        int t = stack.size();
        position[v] = t;
        if (!reach[v]) {
            q = t;
        }
        Set<V> avRemoved = getRemoved(vV);
        Set<E> edgeSet = graph.outgoingEdgesOf(vV);
        for (E e : edgeSet) {
            V wV = graph.getEdgeTarget(e);
            if (avRemoved.contains(wV)) {
                continue;
            }
            int w = toI(wV);
            if (!marked.contains(wV)) {
                boolean gotCycle = cycle(w, q);
                if (gotCycle) {
                    foundCycle = true;
                } else {
                    noCycle(v, w);
                }
            } else if (position[w] <= q) {
                foundCycle = true;
                List<V> cycle = new ArrayList<>();
                Iterator<V> it = stack.descendingIterator();
                V current;
                while (it.hasNext()) {
                    current = it.next();
                    if (wV.equals(current)) {
                        break;
                    }
                }
                cycle.add(wV);
                while (it.hasNext()) {
                    current = it.next();
                    cycle.add(current);
                    if (current.equals(vV)) {
                        break;
                    }
                }
                cycles.add(cycle);
            } else {
                noCycle(v, w);
            }
        }
        stack.pop();
        if (foundCycle) {
            unmark(v);
        }
        reach[v] = true;
        position[v] = graph.vertexSet().size();
        return foundCycle;
    }

    private void noCycle(int x, int y)
    {
        V xV = toV(x);
        V yV = toV(y);

        Set<V> by = getBSet(yV);
        Set<V> axRemoved = getRemoved(xV);

        by.add(xV);
        axRemoved.add(yV);
    }

    private void unmark(int x)
    {
        V xV = toV(x);
        marked.remove(xV);
        Set<V> bx = getBSet(xV);
        for (V yV : bx) {
            Set<V> ayRemoved = getRemoved(yV);
            ayRemoved.remove(xV);
            if (marked.contains(yV)) {
                unmark(toI(yV));
            }
        }
        bx.clear();
    }

    @SuppressWarnings("unchecked")
    private void initState()
    {
        cycles = new ArrayList<>();
        iToV = (V[]) graph.vertexSet().toArray();
        vToI = new HashMap<>();
        bSets = new HashMap<>();
        stack = new ArrayDeque<>();
        marked = new HashSet<>();
        removed = new HashMap<>();
        int size = graph.vertexSet().size();
        position = new int[size];
        reach = new boolean[size];
        startVertices = new ArrayList<>();

        for (int i = 0; i < iToV.length; i++) {
            vToI.put(iToV[i], i);
        }
    }

    private void clearState()
    {
        cycles = null;
        iToV = null;
        vToI = null;
        bSets = null;
        stack = null;
        marked = null;
        removed = null;
        position = null;
        reach = null;
        startVertices = null;
    }

    private Integer toI(V v)
    {
        return vToI.get(v);
    }

    private V toV(int i)
    {
        return iToV[i];
    }

    private Set<V> getBSet(V v)
    {
        // B sets are typically not all
        // needed, so instantiate lazily.
        Set<V> result = bSets.get(v);
        if (result == null) {
            result = new HashSet<>();
            bSets.put(v, result);
        }
        return result;
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


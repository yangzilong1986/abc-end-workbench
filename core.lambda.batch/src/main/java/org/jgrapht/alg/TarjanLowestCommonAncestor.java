package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.util.*;

/**
 * 一种由Robert Tarjan提出的求解有向图强连通分量的线性时间的算法。
 * @param <V>
 * @param <E>
 */
public class TarjanLowestCommonAncestor<V, E>
{
    private Graph<V, E> g;

    /**
     * Create an instance with a reference to the graph that we will find LCAs for
     * 
     * @param g the input graph
     */
    public TarjanLowestCommonAncestor(Graph<V, E> g)
    {
        this.g = g;
    }

    public V calculate(V start, V a, V b)
    {
        List<LcaRequestResponse<V>> list = new LinkedList<>();
        list.add(new LcaRequestResponse<>(a, b));
        return calculate(start, list).get(0);
    }

    public List<V> calculate(V start, List<LcaRequestResponse<V>> lrr)
    {
        return new Worker(lrr).calculate(start);
    }

    /* The worker class keeps the state whilst doing calculations. */
    private class Worker
    {

        private UnionFind<V> uf = new UnionFind<>(Collections.<V> emptySet());

        // the ancestors. instead of <code>u.ancestor = x</code> we do
        // <code>ancestors.put(u,x)</code>
        private Map<V, V> ancestors = new HashMap<>();

        // instead of u.colour = black we do black.add(u)
        private Set<V> black = new HashSet<>();

        // the two vertex that we want to find the LCA for
        private List<LcaRequestResponse<V>> lrr;
        private MultiMap<V> lrrMap;

        private Worker(List<LcaRequestResponse<V>> lrr)
        {
            this.lrr = lrr;
            this.lrrMap = new MultiMap<>();

            // put in the reverse links from a and b entries back to the
            // LcaRequestReponse they're contained in
            for (LcaRequestResponse<V> r : lrr) {
                lrrMap.getOrCreate(r.getA()).add(r);
                lrrMap.getOrCreate(r.getB()).add(r);
            }
        }

        private List<V> calculate(final V u)
        {
            uf.addElement(u);
            ancestors.put(u, u);
            for (E vEdge : g.edgesOf(u)) {
                if (g.getEdgeSource(vEdge).equals(u)) {
                    V v = g.getEdgeTarget(vEdge);
                    calculate(v);
                    uf.union(u, v);
                    ancestors.put(uf.find(u), u);
                }
            }
            black.add(u);

            Set<LcaRequestResponse<V>> requestsForNodeU = lrrMap.get(u);
            if (requestsForNodeU != null) {
                for (LcaRequestResponse<V> rr : requestsForNodeU) {
                    if (black.contains(rr.getB()) && rr.getA().equals(u)) {
                        rr.setLca(ancestors.get(uf.find(rr.getB())));
                    }
                    if (black.contains(rr.getA()) && rr.getB().equals(u)) {
                        rr.setLca(ancestors.get(uf.find(rr.getA())));
                    }
                }

                // once we've dealt with it - remove it (to save memory?)
                lrrMap.remove(u);
            }

            List<V> result = new LinkedList<>();
            for (LcaRequestResponse<V> current : lrr) {
                result.add(current.getLca());
            }
            return result;
        }
    }

    /**
     * Data transfer object for LCA request and response.
     *
     * @param <V> the graph vertex type
     */
    public static class LcaRequestResponse<V>
    {
        private V a, b, lca;

        /**
         * Create a new LCA request response data transfer object.
         * 
         * @param a the first vertex of the request
         * @param b the second vertex of the request
         */
        public LcaRequestResponse(V a, V b)
        {
            this.a = a;
            this.b = b;
        }

        /**
         * Get the first vertex of the request
         * 
         * @return the first vertex of the request
         */
        public V getA()
        {
            return a;
        }

        /**
         * Get the second vertex of the request
         * 
         * @return the second vertex of the request
         */
        public V getB()
        {
            return b;
        }

        /**
         * Get the least common ancestor
         * 
         * @return the least common ancestor
         */
        public V getLca()
        {
            return lca;
        }

        void setLca(V lca)
        {
            this.lca = lca;
        }
    }

    @SuppressWarnings("serial")
    private static final class MultiMap<V>
        extends HashMap<V, Set<LcaRequestResponse<V>>>
    {
        public Set<LcaRequestResponse<V>> getOrCreate(V key)
        {
            if (!containsKey(key)) {
                put(key, new HashSet<>());
            }
            return get(key);
        }
    }
}

// End TarjanLowestCommonAncestor.java

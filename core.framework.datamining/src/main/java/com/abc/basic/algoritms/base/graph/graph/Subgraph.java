package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.AbstractGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.WeightedGraph;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;
import com.abc.basic.algoritms.base.graph.events.GraphEdgeChangeEvent;
import com.abc.basic.algoritms.base.graph.events.GraphListener;
import com.abc.basic.algoritms.base.graph.events.GraphVertexChangeEvent;
import com.abc.basic.algoritms.base.graph.events.ListenableGraph;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Subgraph<V, E, G extends Graph<V, E>>
        extends AbstractGraph<V, E>
        implements Serializable
{
    private static final long serialVersionUID = 3208313055169665387L;

    private static final String NO_SUCH_EDGE_IN_BASE = "no such edge in base graph";
    private static final String NO_SUCH_VERTEX_IN_BASE = "no such vertex in base graph";

    protected final Set<E> edgeSet = new LinkedHashSet<>();
    protected final Set<V> vertexSet = new LinkedHashSet<>();
    protected final G base;
    protected final boolean isInduced;

    private transient Set<E> unmodifiableEdgeSet = null;
    private transient Set<V> unmodifiableVertexSet = null;

    /**
     * Creates a new Subgraph.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>null</code> then all
     *        vertices are included.
     * @param edgeSubset edges to in include in the subgraph. If <code>null</code> then all the
     *        edges whose vertices found in the graph are included.
     */
    public Subgraph(G base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset)
    {
        super();

        this.base = Objects.requireNonNull(base, "Invalid graph provided");
        this.isInduced = edgeSubset == null;

        if (base instanceof ListenableGraph<?, ?>) {
            ((ListenableGraph<V, E>) base).addGraphListener(new BaseGraphListener());
        }

        initialize(vertexSubset, edgeSubset);
    }

    /**
     * Creates a new induced Subgraph. The subgraph will keep track of edges being added to its
     * vertex subset as well as deletion of edges and vertices. If base it not listenable, this is
     * identical to the call Subgraph(base, vertexSubset, null).
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>null</code> then all
     *        vertices are included.
     */
    public Subgraph(G base, Set<? extends V> vertexSubset)
    {
        this(base, vertexSubset, null);
    }

    /**
     * Creates a new induced Subgraph with all vertices included. The subgraph will keep track of
     * edges being added to its vertex subset as well as deletion of edges and vertices. If base it
     * not listenable, this is identical to the call Subgraph(base, null, null).
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     */
    public Subgraph(G base)
    {
        this(base, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        if (containsVertex(sourceVertex) && containsVertex(targetVertex)) {
            return base
                    .getAllEdges(sourceVertex, targetVertex).stream().filter(e -> edgeSet.contains(e))
                    .collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        Set<E> edges = getAllEdges(sourceVertex, targetVertex);

        if (edges == null) {
            return null;
        } else {
            return edges.stream().findAny().orElse(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return base.getEdgeFactory();
    }

    /**
     * Add an edge to the subgraph. The end-points must exist in the subgraph and the edge must
     * exist in the base graph. In case multiple such edges exist in the base graph, one that is not
     * already in the subgraph is chosen arbitrarily and added to the subgraph. In case all such
     * edges already exist in the subgraph, the method returns null.
     *
     * @param sourceVertex the source vertex
     * @param targetVertex the source vertex
     * @return the added edge or null if all such edges from the base graph already belong in the
     *         subgraph
     * @throws IllegalArgumentException if the source or target vertex does not belong to the
     *         subgraph
     * @throws IllegalArgumentException if the base graph does not contain any edge between the two
     *         end-points
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        if (!base.containsEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_IN_BASE);
        }

        Set<E> edges = base.getAllEdges(sourceVertex, targetVertex);

        for (E e : edges) {
            if (!containsEdge(e)) {
                edgeSet.add(e);
                return e;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        if (e == null) {
            throw new NullPointerException();
        }

        if (!base.containsEdge(e)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_IN_BASE);
        }

        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        assert (base.getEdgeSource(e) == sourceVertex);
        assert (base.getEdgeTarget(e) == targetVertex);

        return edgeSet.add(e);
    }

    /**
     * Adds the specified vertex to this subgraph.
     *
     * @param v the vertex to be added.
     *
     * @return <code>true</code> if the vertex was added, otherwise <code>
     * false</code>.
     *
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if the base graph does not contain the vertex
     *
     * @see Subgraph
     * @see Graph#addVertex(Object)
     */
    @Override
    public boolean addVertex(V v)
    {
        if (v == null) {
            throw new NullPointerException();
        }
        if (!base.containsVertex(v)) {
            throw new IllegalArgumentException(NO_SUCH_VERTEX_IN_BASE);
        }
        return vertexSet.add(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(E e)
    {
        return edgeSet.contains(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        return vertexSet.contains(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgeSet()
    {
        if (unmodifiableEdgeSet == null) {
            unmodifiableEdgeSet = Collections.unmodifiableSet(edgeSet);
        }
        return unmodifiableEdgeSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return base.edgesOf(vertex).stream().filter(e -> edgeSet.contains(e)).collect(
                Collectors.toCollection(() -> new LinkedHashSet<>()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        return edgeSet.remove(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        E e = getEdge(sourceVertex, targetVertex);

        return edgeSet.remove(e) ? e : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        // If the base graph does NOT contain v it means we are here in
        // response to removal of v from the base. In such case we don't need
        // to remove all the edges of v as they were already removed.
        if (containsVertex(v) && base.containsVertex(v)) {
            removeAllEdges(edgesOf(v));
        }

        return vertexSet.remove(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        if (unmodifiableVertexSet == null) {
            unmodifiableVertexSet = Collections.unmodifiableSet(vertexSet);
        }

        return unmodifiableVertexSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeSource(E e)
    {
        return base.getEdgeSource(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeTarget(E e)
    {
        return base.getEdgeTarget(e);
    }

    /**
     * Get the base graph.
     *
     * @return the base graph
     */
    public G getBase()
    {
        return base;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E e)
    {
        return base.getEdgeWeight(e);
    }

    public void setEdgeWeight(E e, double weight)
    {
        ((WeightedGraph<V, E>) base).setEdgeWeight(e, weight);
    }

    private void initialize(Set<? extends V> vertexFilter, Set<? extends E> edgeFilter)
    {
        if (vertexFilter == null && edgeFilter == null) {
            vertexSet.addAll(base.vertexSet());
            edgeSet.addAll(base.edgeSet());
            return;
        }

        // add vertices
        if (vertexFilter == null) {
            vertexSet.addAll(base.vertexSet());
        } else {
            if (vertexFilter.size() > base.vertexSet().size()) {
                base.vertexSet().stream().filter(v -> vertexFilter.contains(v)).forEach(
                        v -> vertexSet.add(v));
            } else {
                vertexFilter.stream().filter(v -> v != null && base.containsVertex(v)).forEach(
                        v -> vertexSet.add(v));
            }
        }

        // add edges
        if (edgeFilter == null) {
            base
                    .edgeSet().stream()
                    .filter(
                            e -> vertexSet.contains(base.getEdgeSource(e))
                                    && vertexSet.contains(base.getEdgeTarget(e)))
                    .forEach(e -> edgeSet.add(e));
        } else {
            if (edgeFilter.size() > base.edgeSet().size()) {
                base
                        .edgeSet().stream()
                        .filter(
                                e -> edgeFilter.contains(e) && vertexSet.contains(base.getEdgeSource(e))
                                        && vertexSet.contains(base.getEdgeTarget(e)))
                        .forEach(e -> edgeSet.add(e));
            } else {
                edgeFilter
                        .stream()
                        .filter(
                                e -> e != null && base.containsEdge(e)
                                        && vertexSet.contains(base.getEdgeSource(e))
                                        && vertexSet.contains(base.getEdgeTarget(e)))
                        .forEach(e -> edgeSet.add(e));
            }
        }
    }

    /**
     * An internal listener on the base graph.
     *
     * @author Barak Naveh
     * @since Jul 20, 2003
     */
    private class BaseGraphListener
            implements GraphListener<V, E>, Serializable
    {
        private static final long serialVersionUID = 4343535244243546391L;

        /**
         * {@inheritDoc}
         */
        @Override
        public void edgeAdded(GraphEdgeChangeEvent<V, E> e)
        {
            if (isInduced) {
                E edge = e.getEdge();
                V source = e.getEdgeSource();
                V target = e.getEdgeTarget();
                if (containsVertex(source) && containsVertex(target)) {
                    addEdge(source, target, edge);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void edgeRemoved(GraphEdgeChangeEvent<V, E> e)
        {
            E edge = e.getEdge();

            removeEdge(edge);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexAdded(GraphVertexChangeEvent<V> e)
        {
            // we don't care
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexRemoved(GraphVertexChangeEvent<V> e)
        {
            V vertex = e.getVertex();

            removeVertex(vertex);
        }
    }
}

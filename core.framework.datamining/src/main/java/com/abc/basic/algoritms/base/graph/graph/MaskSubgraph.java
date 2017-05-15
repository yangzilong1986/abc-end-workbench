package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.AbstractGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class MaskSubgraph<V, E>
        extends AbstractGraph<V, E>
{
    private static final String UNMODIFIABLE = "this graph is unmodifiable";

    protected final Graph<V, E> base;
    protected final Set<E> edges;
    protected final Set<V> vertices;
    protected final Predicate<V> vertexMask;
    protected final Predicate<E> edgeMask;

    /**
     * Creates a new induced subgraph. Running-time = O(1).
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param mask vertices and edges to exclude in the subgraph. If a vertex/edge is masked, it is
     *        as if it is not in the subgraph.
     * @deprecated in favor of using the constructor with lambdas
     */
//    @Deprecated
//    public MaskSubgraph(Graph<V, E> base, MaskFunctor<V, E> mask)
//    {
//        this(base, v -> mask.isVertexMasked(v), e -> mask.isEdgeMasked(e));
//    }

    /**
     * Creates a new induced subgraph. Running-time = O(1).
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexMask vertices to exclude in the subgraph. If a vertex is masked, it is as if it
     *        is not in the subgraph. Edges incident to the masked vertex are also masked.
     * @param edgeMask edges to exclude in the subgraph. If an edge is masked, it is as if it is not
     *        in the subgraph.
     */
    public MaskSubgraph(Graph<V, E> base, Predicate<V> vertexMask, Predicate<E> edgeMask)
    {
        super();
        this.base = Objects.requireNonNull(base, "Invalid graph provided");
        this.vertexMask = Objects.requireNonNull(vertexMask, "Invalid vertex mask provided");
        this.edgeMask = Objects.requireNonNull(edgeMask, "Invalid edge mask provided");
        this.vertices = new MaskVertexSet<>(base.vertexSet(), vertexMask);
        this.edges = new MaskEdgeSet<>(base, base.edgeSet(), vertexMask, edgeMask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E edge)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(V v)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(E e)
    {
        return edgeSet().contains(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        return vertexSet().contains(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgeSet()
    {
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return new MaskEdgeSet<>(base, base.edgesOf(vertex), vertexMask, edgeMask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        if (containsVertex(sourceVertex) && containsVertex(targetVertex)) {
            return new MaskEdgeSet<>(
                    base, base.getAllEdges(sourceVertex, targetVertex), vertexMask, edgeMask);
        } else
            return null;
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
     * {@inheritDoc}
     */
    @Override
    public V getEdgeSource(E edge)
    {
        assert (edgeSet().contains(edge));

        return base.getEdgeSource(edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeTarget(E edge)
    {
        assert (edgeSet().contains(edge));

        return base.getEdgeTarget(edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E edge)
    {
        assert (edgeSet().contains(edge));

        return base.getEdgeWeight(edge);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        return vertices;
    }
}


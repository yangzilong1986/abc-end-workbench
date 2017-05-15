package org.jgrapht.alg.shortestpath;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.util.*;

class DijkstraClosestFirstIterator<V, E>
    implements Iterator<V>
{
    private final Graph<V, E> graph;
    private final V source;
    private final double radius;
    private final Specifics specifics;
    private final FibonacciHeap<QueueEntry> heap;
    private final Map<V, FibonacciHeapNode<QueueEntry>> seen;

    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source)
    {
        this(graph, source, Double.POSITIVE_INFINITY);
    }

    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source, double radius)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.source = Objects.requireNonNull(source, "Sourve vertex cannot be null");
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }
        this.radius = radius;
        if (graph instanceof DirectedGraph) {
            this.specifics = new DirectedSpecifics((DirectedGraph<V, E>) graph);
        } else {
            this.specifics = new UndirectedSpecifics(graph);
        }
        this.heap = new FibonacciHeap<>();
        this.seen = new HashMap<>();

        // initialize with source vertex
        updateDistance(source, null, 0d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext()
    {
        if (heap.isEmpty()) {
            return false;
        }
        FibonacciHeapNode<QueueEntry> vNode = heap.min();
        double vDistance = vNode.getKey();
        if (radius < vDistance) {
            heap.clear();
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V next()
    {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // settle next node
        FibonacciHeapNode<QueueEntry> vNode = heap.removeMin();
        V v = vNode.getData().v;
        double vDistance = vNode.getKey();

        // relax edges
        for (E e : specifics.edgesOf(v)) {
            V u = Graphs.getOppositeVertex(graph, e, v);
            double eWeight = graph.getEdgeWeight(e);
            if (eWeight < 0.0) {
                throw new IllegalArgumentException("Negative edge weight not allowed");
            }
            updateDistance(u, e, vDistance + eWeight);
        }

        return v;
    }

    public SingleSourcePaths<V, E> getPaths()
    {
        Map<V, Pair<Double, E>> distanceAndPredecessorMap = new HashMap<>();

        for (FibonacciHeapNode<QueueEntry> vNode : seen.values()) {
            double vDistance = vNode.getKey();
            if (radius < vDistance) {
                continue;
            }
            V v = vNode.getData().v;
            distanceAndPredecessorMap.put(v, Pair.of(vDistance, vNode.getData().e));
        }

        return new TreeSingleSourcePathsImpl<>(graph, source, distanceAndPredecessorMap);
    }

    private void updateDistance(V v, E e, double distance)
    {
        FibonacciHeapNode<QueueEntry> node = seen.get(v);
        if (node == null) {
            node = new FibonacciHeapNode<>(new QueueEntry(e, v));
            heap.insert(node, distance);
            seen.put(v, node);
        } else {
            if (distance < node.getKey()) {
                heap.decreaseKey(node, distance);
                node.getData().e = e;
            }
        }
    }

    abstract class Specifics
    {
        public abstract Set<? extends E> edgesOf(V vertex);
    }

    class DirectedSpecifics
        extends Specifics
    {

        private DirectedGraph<V, E> graph;

        public DirectedSpecifics(DirectedGraph<V, E> g)
        {
            graph = g;
        }

        @Override
        public Set<? extends E> edgesOf(V vertex)
        {
            return graph.outgoingEdgesOf(vertex);
        }
    }

    class UndirectedSpecifics
        extends Specifics
    {

        private Graph<V, E> graph;

        public UndirectedSpecifics(Graph<V, E> g)
        {
            graph = g;
        }

        @Override
        public Set<E> edgesOf(V vertex)
        {
            return graph.edgesOf(vertex);
        }
    }

    class QueueEntry
    {
        E e;
        V v;

        public QueueEntry(E e, V v)
        {
            this.e = e;
            this.v = v;
        }
    }
}

package com.abc.basic.algoritms.base.graph.shortestpath.bellmanford;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;
import com.abc.basic.algoritms.base.graph.UndirectedGraph;

import java.util.*;

class BellmanFordIterator<V, E>
        implements Iterator<List<V>>
{
    /**
     * Error message.
     */
    protected final static String NEGATIVE_UNDIRECTED_EDGE =
            "Negative" + "edge-weights are not allowed in an unidrected graph!";

    /**
     * Graph on which shortest paths are searched.
     */
    protected Graph<V, E> graph;

    /**
     * Start vertex.
     */
    protected V startVertex;

    /**
     * Vertices whose shortest path cost have been improved during the previous pass.
     */
    private List<V> prevImprovedVertices = new ArrayList<>();

    private Map<V, BellmanFordPathElement<V, E>> prevVertexData;

    private boolean startVertexEncountered = false;

    private Map<V, BellmanFordPathElement<V, E>> vertexData;

    private double epsilon;

    protected BellmanFordIterator(Graph<V, E> graph, V startVertex, double epsilon)
    {
        assertBellmanFordIterator(graph, startVertex);

        this.graph = graph;
        this.startVertex = startVertex;
        this.epsilon = epsilon;
    }

    public BellmanFordPathElement<V, E> getPathElement(V endVertex)
    {
        return getSeenData(endVertex);
    }

    @Override
    public boolean hasNext()
    {
        if (!this.startVertexEncountered) {
            encounterStartVertex();
        }

        return !(this.prevImprovedVertices.isEmpty());
    }

    @Override
    public List<V> next()
    {
        if (!this.startVertexEncountered) {
            encounterStartVertex();
        }

        if (hasNext()) {
            List<V> improvedVertices = new ArrayList<>();
            for (int i = this.prevImprovedVertices.size() - 1; i >= 0; i--) {
                V vertex = this.prevImprovedVertices.get(i);
                for (Iterator<? extends E> iter = edgesOfIterator(vertex); iter.hasNext();) {
                    E edge = iter.next();
                    V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
                    if (getPathElement(oppositeVertex) != null) {
                        //松弛算法
                        boolean relaxed = relaxVertexAgain(oppositeVertex, edge);
                        if (relaxed) {
                            improvedVertices.add(oppositeVertex);
                        }
                    } else {
                        relaxVertex(oppositeVertex, edge);
                        improvedVertices.add(oppositeVertex);
                    }
                }
            }

            savePassData(improvedVertices);

            return improvedVertices;
        }

        throw new NoSuchElementException();
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    protected void assertValidEdge(E edge)
    {
        if (this.graph instanceof UndirectedGraph<?, ?>) {
            if (graph.getEdgeWeight(edge) < 0) {
                throw new IllegalArgumentException(NEGATIVE_UNDIRECTED_EDGE);
            }
        }
    }

    protected double calculatePathCost(V vertex, E edge)
    {
        V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);

        // we get the data of the previous pass.
        BellmanFordPathElement<V, E> oppositePrevData = getPrevSeenData(oppositeVertex);

        double pathCost = graph.getEdgeWeight(edge);

        if (!oppositePrevData.getVertex().equals(this.startVertex)) {
            // if it's not the start vertex, we add the cost of the previous
            // pass.
            pathCost += oppositePrevData.getCost();
        }

        return pathCost;
    }

    protected Iterator<E> edgesOfIterator(V vertex)
    {
        if (this.graph instanceof DirectedGraph<?, ?>) {
            return ((DirectedGraph<V, E>) this.graph).outgoingEdgesOf(vertex).iterator();
        } else {
            return this.graph.edgesOf(vertex).iterator();
        }
    }

    protected BellmanFordPathElement<V, E> getPrevSeenData(V vertex)
    {
        return this.prevVertexData.get(vertex);
    }


    protected BellmanFordPathElement<V, E> getSeenData(V vertex)
    {
        return this.vertexData.get(vertex);
    }

    protected boolean isSeenVertex(V vertex)
    {
        return this.vertexData.containsKey(vertex);
    }


    protected BellmanFordPathElement<V, E> putPrevSeenData(
            V vertex, BellmanFordPathElement<V, E> data)
    {
        if (this.prevVertexData == null) {
            this.prevVertexData = new HashMap<>();
        }

        return this.prevVertexData.put(vertex, data);
    }

    protected BellmanFordPathElement<V, E> putSeenData(V vertex, BellmanFordPathElement<V, E> data)
    {
        if (this.vertexData == null) {
            this.vertexData = new HashMap<>();
        }

        return this.vertexData.put(vertex, data);
    }

    private void assertBellmanFordIterator(Graph<V, E> graph, V startVertex)
    {
        if (!(graph.containsVertex(startVertex))) {
            throw new IllegalArgumentException("Graph must contain the start vertex!");
        }
    }

    private BellmanFordPathElement<V, E> createSeenData(V vertex, E edge, double cost)
    {
        BellmanFordPathElement<V, E> prevPathElement =
                getPrevSeenData(Graphs.getOppositeVertex(graph, edge, vertex));

        return new BellmanFordPathElement<>(graph, prevPathElement, edge, cost, epsilon);
    }

    private void encounterStartVertex()
    {
        BellmanFordPathElement<V, E> data = new BellmanFordPathElement<>(this.startVertex, epsilon);

        // first the only vertex considered as improved is the start vertex.
        this.prevImprovedVertices.add(this.startVertex);

        putSeenData(this.startVertex, data);
        putPrevSeenData(this.startVertex, data);

        this.startVertexEncountered = true;
    }


    private void relaxVertex(V vertex, E edge)
    {
        assertValidEdge(edge);

        double shortestPathCost = calculatePathCost(vertex, edge);

        BellmanFordPathElement<V, E> data = createSeenData(vertex, edge, shortestPathCost);

        putSeenData(vertex, data);
    }

    private boolean relaxVertexAgain(V vertex, E edge)
    {
        assertValidEdge(edge);

        double candidateCost = calculatePathCost(vertex, edge);

        // we get the data of the previous pass.
        BellmanFordPathElement<V, E> oppositePrevData =
                getPrevSeenData(Graphs.getOppositeVertex(graph, edge, vertex));

        BellmanFordPathElement<V, E> pathElement = getSeenData(vertex);
        return pathElement.improve(oppositePrevData, edge, candidateCost);
    }

    private void savePassData(List<V> improvedVertices)
    {
        for (V vertex : improvedVertices) {
            BellmanFordPathElement<V, E> orig = getSeenData(vertex);
            BellmanFordPathElement<V, E> clonedData = new BellmanFordPathElement<>(orig);
            putPrevSeenData(vertex, clonedData);
        }

        this.prevImprovedVertices = improvedVertices;
    }
}


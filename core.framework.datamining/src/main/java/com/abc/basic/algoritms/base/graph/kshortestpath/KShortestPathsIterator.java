package com.abc.basic.algoritms.base.graph.kshortestpath;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;

import java.util.*;

class KShortestPathsIterator<V, E>
        implements Iterator<Set<V>>
{

    private V endVertex;


    private Graph<V, E> graph;

    /**
     * Number of paths stored at each end vertex.
     */
    private int k;

    private Set<V> prevImprovedVertices;

    /**
     * Stores the paths that improved the vertex in the previous pass.
     */
    private Map<V, RankingPathElementList<V, E>> prevSeenDataContainer;

    private Map<V, RankingPathElementList<V, E>> seenDataContainer;

    private PathValidator<V, E> pathValidator = null;

    /**
     * Start vertex.
     */
    private V startVertex;

    private boolean startVertexEncountered;

    /**
     * Stores the number of the path.
     */
    private int passNumber = 1;

    public KShortestPathsIterator(Graph<V, E> graph, V startVertex, V endVertex, int maxSize)
    {
        this(graph, startVertex, endVertex, maxSize, null);
    }

    public KShortestPathsIterator(
            Graph<V, E> graph, V startVertex, V endVertex, int maxSize,
            PathValidator<V, E> pathValidator)
    {
        assertKShortestPathsIterator(graph, startVertex);

        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;

        this.k = maxSize;

        this.seenDataContainer = new HashMap<>();
        this.prevSeenDataContainer = new HashMap<>();

        this.prevImprovedVertices = new HashSet<>();
        this.pathValidator = pathValidator;
    }

    /**
     * @return <code>true</code> if at least one path has been improved during the previous pass,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean hasNext()
    {
        if (!this.startVertexEncountered) {
            encounterStartVertex();
        }

        return !(this.prevImprovedVertices.isEmpty());
    }

    @Override
    public Set<V> next()
    {
        if (!this.startVertexEncountered) {
            encounterStartVertex();
        }

        // at the i-th pass the shortest paths with i edges are calculated.
        if (hasNext()) {
            Set<V> improvedVertices = new HashSet<>();

            for (V vertex : this.prevImprovedVertices) {
                if (!vertex.equals(this.endVertex)) {
                    updateOutgoingVertices(vertex, improvedVertices);
                }
            }

            savePassData(improvedVertices);
            this.passNumber++;

            return improvedVertices;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    RankingPathElementList<V, E> getPathElements(V endVertex)
    {
        return this.seenDataContainer.get(endVertex);
    }

    private void assertKShortestPathsIterator(Graph<V, E> graph, V startVertex)
    {
        if (graph == null) {
            throw new NullPointerException("graph is null");
        }
        if (startVertex == null) {
            throw new NullPointerException("startVertex is null");
        }
    }

    private RankingPathElementList<V, E> createSeenData(V vertex, E edge)
    {
        V oppositeVertex = Graphs.getOppositeVertex(this.graph, edge, vertex);

        RankingPathElementList<V, E> oppositeData = this.prevSeenDataContainer.get(oppositeVertex);

        // endVertex in argument to ensure that stored paths do not disconnect
        // the end-vertex

        return new RankingPathElementList<>(
                this.graph, this.k, oppositeData, edge, this.endVertex, this.pathValidator);
    }

    /**
     * Returns an iterator to loop over outgoing edges <code>Edge</code> of the vertex.
     *
     * @param vertex
     *
     * @return .
     */
    private Iterator<E> edgesOfIterator(V vertex)
    {
        if (this.graph instanceof DirectedGraph<?, ?>) {
            return ((DirectedGraph<V, E>) this.graph).outgoingEdgesOf(vertex).iterator();
        } else {
            return this.graph.edgesOf(vertex).iterator();
        }
    }

    /**
     * Initializes the list of paths at the start vertex and adds an empty path.
     */
    private void encounterStartVertex()
    {
        RankingPathElementList<V, E> data = new RankingPathElementList<>(
                this.graph, this.k, new RankingPathElement<>(this.startVertex), this.pathValidator);

        this.seenDataContainer.put(this.startVertex, data);
        this.prevSeenDataContainer.put(this.startVertex, data);

        // initially the only vertex whose value is considered to have changed
        // is the start vertex
        this.prevImprovedVertices.add(this.startVertex);

        this.startVertexEncountered = true;
    }

    private void savePassData(Set<V> improvedVertices)
    {
        for (V vertex : improvedVertices) {
            RankingPathElementList<V, E> pathElementList = this.seenDataContainer.get(vertex);

            RankingPathElementList<V, E> improvedPaths = new RankingPathElementList<>(
                    this.graph, pathElementList.maxSize, vertex, this.pathValidator);

            for (RankingPathElement<V, E> path : pathElementList) {
                if (path.getHopCount() == this.passNumber) {
                    // the path has just been computed.
                    improvedPaths.pathElements.add(path);
                }
            }

            this.prevSeenDataContainer.put(vertex, improvedPaths);
        }

        this.prevImprovedVertices = improvedVertices;
    }

    /**
     * Try to add the first paths to the specified vertex. These paths reached the specified vertex
     * and ended with the specified edge. A new intermediary path is stored in the paths list of the
     * specified vertex provided that the path can be extended to the end-vertex.
     *
     * @param vertex vertex reached by a path.
     * @param edge edge reaching the vertex.
     */
    private boolean tryToAddFirstPaths(V vertex, E edge)
    {
        // the vertex has not been reached yet
        RankingPathElementList<V, E> data = createSeenData(vertex, edge);

        if (!data.isEmpty()) {
            this.seenDataContainer.put(vertex, data);
            return true;
        }
        return false;
    }

    /**
     * Try to add new paths for the vertex. These new paths reached the specified vertex and ended
     * with the specified edge. A new intermediary path is stored in the paths list of the specified
     * vertex provided that the path can be extended to the end-vertex.
     *
     * @param vertex a vertex which has just been encountered.
     * @param edge the edge via which the vertex was encountered.
     */
    private boolean tryToAddNewPaths(V vertex, E edge)
    {
        RankingPathElementList<V, E> data = this.seenDataContainer.get(vertex);

        V oppositeVertex = Graphs.getOppositeVertex(this.graph, edge, vertex);
        RankingPathElementList<V, E> oppositeData = this.prevSeenDataContainer.get(oppositeVertex);

        return data.addPathElements(oppositeData, edge);
    }

    private void updateOutgoingVertices(V vertex, Set<V> improvedVertices)
    {
        // try to add new paths for the target vertices of the outgoing edges
        // of the vertex in argument.
        for (Iterator<E> iter = edgesOfIterator(vertex); iter.hasNext();) {
            E edge = iter.next();
            V vertexReachedByEdge = Graphs.getOppositeVertex(this.graph, edge, vertex);

            // check if the path does not loop over the start vertex.
            if (!vertexReachedByEdge.equals(this.startVertex)) {
                if (this.seenDataContainer.containsKey(vertexReachedByEdge)) {
                    boolean relaxed = tryToAddNewPaths(vertexReachedByEdge, edge);
                    if (relaxed) {
                        improvedVertices.add(vertexReachedByEdge);
                    }
                } else {
                    boolean relaxed = tryToAddFirstPaths(vertexReachedByEdge, edge);
                    if (relaxed) {
                        improvedVertices.add(vertexReachedByEdge);
                    }
                }
            }
        }
    }
}

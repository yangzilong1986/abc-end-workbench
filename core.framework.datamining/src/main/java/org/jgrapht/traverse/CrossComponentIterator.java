package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.event.*;

public abstract class CrossComponentIterator<V, E, D>
    extends AbstractGraphIterator<V, E>
{
    private static final int CCS_BEFORE_COMPONENT = 1;
    private static final int CCS_WITHIN_COMPONENT = 2;
    private static final int CCS_AFTER_COMPONENT = 3;

    /**
     * Standard vertex visit state enumeration.
     */
    protected static enum VisitColor
    {
        /**
         * Vertex has not been returned via iterator yet.
         */
        WHITE,

        /**
         * Vertex has been returned via iterator, but we're not done with all of its out-edges yet.
         */
        GRAY,

        /**
         * Vertex has been returned via iterator, and we're done with all of its out-edges.
         */
        BLACK
    }

    //
    private final ConnectedComponentTraversalEvent ccFinishedEvent =
        new ConnectedComponentTraversalEvent(
            this, ConnectedComponentTraversalEvent.CONNECTED_COMPONENT_FINISHED);
    private final ConnectedComponentTraversalEvent ccStartedEvent =
        new ConnectedComponentTraversalEvent(
            this, ConnectedComponentTraversalEvent.CONNECTED_COMPONENT_STARTED);

    private Iterator<V> vertexIterator = null;

    /**
     * Stores the vertices that have been seen during iteration and (optionally) some additional
     * traversal info regarding each vertex.
     */
    private Map<V, D> seen = new HashMap<>();
    private V startVertex;

    private final Graph<V, E> graph;

    /**
     * The connected component state
     */
    private int state = CCS_BEFORE_COMPONENT;

    public CrossComponentIterator(Graph<V, E> g, V startVertex)
    {
        super();

        if (g == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
        graph = g;

        specifics = createGraphSpecifics(g);
        vertexIterator = g.vertexSet().iterator();
        setCrossComponentTraversal(startVertex == null);

        reusableEdgeEvent = new FlyweightEdgeEvent<>(this, null);
        reusableVertexEvent = new FlyweightVertexEvent<>(this, null);

        if (startVertex == null) {
            // pick a start vertex if graph not empty
            if (vertexIterator.hasNext()) {
                this.startVertex = vertexIterator.next();
            } else {
                this.startVertex = null;
            }
        } else if (g.containsVertex(startVertex)) {
            this.startVertex = startVertex;
        } else {
            throw new IllegalArgumentException("graph must contain the start vertex");
        }
    }

    /**
     * @return the graph being traversed
     */
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext()
    {
        if (startVertex != null) {//构造方法构造的图的第一个节点
            encounterStartVertex();//调用私有方法，进而调用子类，获取第一个顶点
        }
        //子类方法，是否有顶点
        if (isConnectedComponentExhausted()) {//没有元素时则执行
            if (state == CCS_WITHIN_COMPONENT) {
                state = CCS_AFTER_COMPONENT;
                if (nListeners != 0) {
                    fireConnectedComponentFinished(ccFinishedEvent);
                }
            }

            if (isCrossComponentTraversal()) {
                while (vertexIterator.hasNext()) {//Graph的节点向下一个移动
                    V v = vertexIterator.next();

                    if (!isSeenVertex(v)) {
                        encounterVertex(v, null);
                        state = CCS_BEFORE_COMPONENT;

                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public V next()
    {
        if (startVertex != null) {
            //调用私有方法，进而调用子类，获取第一个顶点
            encounterStartVertex();
        }

        if (hasNext()) {//是否有下一个节点
            if (state == CCS_BEFORE_COMPONENT) {
                state = CCS_WITHIN_COMPONENT;
                if (nListeners != 0) {
                    fireConnectedComponentStarted(ccStartedEvent);
                }
            }
            //子类方法，获取下一个边
            V nextVertex = provideNextVertex();
            if (nListeners != 0) {
                fireVertexTraversed(createVertexTraversalEvent(nextVertex));
            }
            //私有方法
            addUnseenChildrenOf(nextVertex);

            return nextVertex;
        } else {
            throw new NoSuchElementException();
        }
    }

    protected abstract boolean isConnectedComponentExhausted();

    protected abstract void encounterVertex(V vertex, E edge);

    protected abstract V provideNextVertex();


    protected D getSeenData(V vertex)
    {
        return seen.get(vertex);
    }


    protected boolean isSeenVertex(V vertex)
    {
        return seen.containsKey(vertex);
    }

    protected abstract void encounterVertexAgain(V vertex, E edge);

    protected D putSeenData(V vertex, D data)
    {
        return seen.put(vertex, data);
    }

    protected void finishVertex(V vertex)
    {
        if (nListeners != 0) {
            fireVertexFinished(createVertexTraversalEvent(vertex));
        }
    }

    private void addUnseenChildrenOf(V vertex)
    {
        //从顶点中获取边
        for (E edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            V oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            //是否添加到已经方法的Map
            if (isSeenVertex(oppositeV)) {
                //调用子类方法
                encounterVertexAgain(oppositeV, edge);
            } else {
                //调用子类方法
                encounterVertex(oppositeV, edge);
            }
        }
    }

    private EdgeTraversalEvent<E> createEdgeTraversalEvent(E edge)
    {
        if (isReuseEvents()) {
            reusableEdgeEvent.setEdge(edge);

            return reusableEdgeEvent;
        } else {
            return new EdgeTraversalEvent<>(this, edge);
        }
    }

    private VertexTraversalEvent<V> createVertexTraversalEvent(V vertex)
    {
        if (isReuseEvents()) {
            reusableVertexEvent.setVertex(vertex);

            return reusableVertexEvent;
        } else {
            return new VertexTraversalEvent<>(this, vertex);
        }
    }

    private void encounterStartVertex()
    {
        //子类实现
        encounterVertex(startVertex, null);
        startVertex = null;
    }

    static interface SimpleContainer<T>
    {
        public boolean isEmpty();

        public void add(T o);

        public T remove();
    }

}

// End CrossComponentIterator.java

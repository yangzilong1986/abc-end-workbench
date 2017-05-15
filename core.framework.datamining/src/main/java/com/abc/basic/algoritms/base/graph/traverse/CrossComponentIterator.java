package com.abc.basic.algoritms.base.graph.traverse;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class CrossComponentIterator<V, E, D>
        extends AbstractGraphIterator<V, E>
{
    private static final int CCS_BEFORE_COMPONENT = 1;
    private static final int CCS_WITHIN_COMPONENT = 2;
    private static final int CCS_AFTER_COMPONENT = 3;

    protected static enum VisitColor
    {
        WHITE,
        GRAY,
        BLACK
    }

    private Iterator<V> vertexIterator = null;

    //
    private Map<V, D> seen = new HashMap<>();

    //第一个顶点
    private V startVertex;

    private final Graph<V, E> graph;


    private int state = CCS_BEFORE_COMPONENT;

    public CrossComponentIterator(Graph<V, E> g, V startVertex)
    {
        super();

        if (g == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
        graph = g;

        specifics = createGraphSpecifics(g);
        //在图中获取边的遍历器，获取顶点
        vertexIterator = g.vertexSet().iterator();
        setCrossComponentTraversal(startVertex == null);

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

    public Graph<V, E> getGraph()
    {
        return graph;
    }

    @Override
    public boolean hasNext()
    {
        if (startVertex != null) {//构造方法构造的图的第一个节点
            //子类实现
            encounterVertex(startVertex, null);
            startVertex = null;
        }
        //子类方法，是否有顶点
        //有子类返回是否还有元素
        if (isConnectedComponentExhausted()) {//没有元素时则执行
            //是否有元素
            if (isCrossComponentTraversal()) {
                while (vertexIterator.hasNext()) {//Graph的节点向下一个移动
                    V v = vertexIterator.next();
                    if (!seen.containsKey(v)) {//不包括此节点
                        //子方法
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

    @Override
    public V next()
    {
        if (startVertex != null) {
            //子类实现
            encounterVertex(startVertex, null);
            startVertex = null;
        }
        //子类中有元素
        if (hasNext()) {//是否有下一个节点
           //子类方法，provideNextVertex
            //nextVertex返回值
            V nextVertex = provideNextVertex();
            //私有方法
            //获取下一个节点
            addUnseenChildrenOf(nextVertex);

            return nextVertex;
        } else {
            throw new NoSuchElementException();
        }
    }

    private void addUnseenChildrenOf(V vertex)
    {
        //从顶点中获取边
        for (E edge : specifics.edgesOf(vertex)) {
            V oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            //是否添加到已经方法的Map
            if (seen.containsKey(oppositeV)) {//已经存在的结点
                //调用子类方法
                encounterVertexAgain(oppositeV, edge);
            } else {
                //调用子类方法
                encounterVertex(oppositeV, edge);
            }
        }
    }

    protected abstract boolean isConnectedComponentExhausted();

    protected abstract void encounterVertex(V vertex, E edge);

    protected abstract V provideNextVertex();

    protected abstract void encounterVertexAgain(V vertex, E edge);


    ////////////////////////////////////////
    protected D getSeenData(V vertex)
    {
        return seen.get(vertex);
    }

    /**
     * 已经访问过的顶点
     * @param vertex
     * @param data
     * @return
     */
    protected D putSeenData(V vertex, D data)
    {
        return seen.put(vertex, data);
    }
    //////////////////////////////////////////////////////
}

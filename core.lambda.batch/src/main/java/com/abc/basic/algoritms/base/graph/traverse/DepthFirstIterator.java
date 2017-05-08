package com.abc.basic.algoritms.base.graph.traverse;


import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.util.TypeUtil;

import java.util.ArrayDeque;
import java.util.Deque;

public class DepthFirstIterator<V, E>
        extends CrossComponentIterator<V, E, CrossComponentIterator.VisitColor>
{
    //哨兵
    public static final Object SENTINEL = new Object();

    private Deque<Object> stack = new ArrayDeque<>();

    private transient TypeUtil<V> vertexTypeDecl = null;

    public DepthFirstIterator(Graph<V, E> g)
    {
        this(g, null);
    }

    public DepthFirstIterator(Graph<V, E> g, V startVertex)
    {
        super(g, startVertex);
    }

    @Override
    protected boolean isConnectedComponentExhausted()
    {
        for (;;) {
            if (stack.isEmpty()) {
                return true;
            }
            Object sObject=stack.getLast();
            if (sObject != SENTINEL) {//最后一个不为哨兵对象，则表示非空
                return false;
            }
            //删除哨兵元素
            stack.removeLast();
            //
            recordFinish();
        }
    }

    @Override
    /**
     * 两个访问顶点的方法之一，已经访问过
     */
    protected void encounterVertexAgain(V vertex, E edge)
    {
        VisitColor color = getSeenData(vertex);
        if (color != VisitColor.WHITE) {//白色
            return;//不是白色直接返回，如I->C
        }
        boolean found = stack.removeLastOccurrence(vertex);
        assert (found);
        stack.addLast(vertex);
    }

    @Override
    /**
     * 两个访问顶点的方法之一，没有访问过
     */
    protected void encounterVertex(V vertex, E edge)
    {
        putSeenData(vertex, VisitColor.WHITE);
        stack.addLast(vertex);
    }


    @Override
    protected V provideNextVertex()
    {
        V v;
        for (;;) {
            Object o = stack.removeLast();
            if (o == SENTINEL) {//是哨兵时取出两个数据
                recordFinish();
            } else {
                v = TypeUtil.uncheckedCast(o, vertexTypeDecl);
                break;
            }
        }
        //添加一个默认的哨兵，绕过hasNext
        stack.addLast(v);
        stack.addLast(SENTINEL);//节点已经访问过
        putSeenData(v, VisitColor.GRAY);//灰色
        return v;
    }

    private void recordFinish()
    {
        Object object=stack.removeLast();
        V v = TypeUtil.uncheckedCast(object, vertexTypeDecl);
        putSeenData(v, VisitColor.BLACK);//黑色
    }

}


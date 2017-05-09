package com.abc.basic.algoritms.base.graph.events;

public class GraphChangeEvent
        extends EventObject
{
    private static final long serialVersionUID = 3834592106026382391L;

    protected int type;

    public GraphChangeEvent(Object eventSource, int type)
    {
        super(eventSource);
        this.type = type;
    }
    public int getType()
    {
        return type;
    }
}

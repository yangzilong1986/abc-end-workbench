package com.abc.basic.algoritms.base.graph;

public class DefaultEdge extends IntrusiveEdge
{
    private static final long serialVersionUID = 3258408452177932855L;

    protected Object getSource()
    {
        return source;
    }

    protected Object getTarget()
    {
        return target;
    }

    @Override
    public String toString()
    {
        return "(" + source + " : " + target + ")";
    }
}
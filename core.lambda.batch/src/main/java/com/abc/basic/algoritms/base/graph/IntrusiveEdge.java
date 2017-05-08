package com.abc.basic.algoritms.base.graph;

import java.io.*;

class IntrusiveEdge
        implements Cloneable, Serializable
{
    private static final long serialVersionUID = 3258408452177932855L;

    Object source;

    Object target;

    @Override
    public Object clone()
    {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // shouldn't happen as we are Cloneable
            throw new InternalError();
        }
    }
}


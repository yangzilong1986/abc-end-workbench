package com.abc.basic.algoritms.base.graph.util;


public class TypeUtil<T>
{

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object o, TypeUtil<T> typeDecl)
    {
        return (T) o;
    }
}


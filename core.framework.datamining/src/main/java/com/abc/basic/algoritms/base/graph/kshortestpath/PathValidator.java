package com.abc.basic.algoritms.base.graph.kshortestpath;

import com.abc.basic.algoritms.base.graph.shortestpath.AbstractPathElement;

public interface PathValidator<V, E>
{

    public boolean isValidPath(AbstractPathElement<V, E> prevPathElement, E edge);
}

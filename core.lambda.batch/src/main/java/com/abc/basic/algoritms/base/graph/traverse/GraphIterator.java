package com.abc.basic.algoritms.base.graph.traverse;

import java.util.Iterator;

public interface GraphIterator<V, E>
        extends Iterator<V>
{

    boolean isCrossComponentTraversal();

    void setReuseEvents(boolean reuseEvents);

    boolean isReuseEvents();
    /**
     * Unsupported.
     */
    @Override
    void remove();

}

/*
 * (C) Copyright 2003-2017, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.event.*;

public interface GraphIterator<V, E>
    extends Iterator<V>
{

    boolean isCrossComponentTraversal();

    void setReuseEvents(boolean reuseEvents);

    boolean isReuseEvents();

    void addTraversalListener(TraversalListener<V, E> l);

    /**
     * Unsupported.
     */
    @Override
    void remove();

    void removeTraversalListener(TraversalListener<V, E> l);
}

// End GraphIterator.java

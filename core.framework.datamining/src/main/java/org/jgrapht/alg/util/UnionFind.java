package org.jgrapht.alg.util;

import java.util.*;

public class UnionFind<T>
{
    private Map<T, T> parentMap;
    private Map<T, Integer> rankMap;

    /**
     * Creates a UnionFind instance with all the elements in separate sets.
     * 
     * @param elements the initial elements to include (each element in a singleton set).
     */
    public UnionFind(Set<T> elements)
    {
        parentMap = new HashMap<>();
        rankMap = new HashMap<>();
        for (T element : elements) {
            parentMap.put(element, element);
            rankMap.put(element, 0);
        }
    }

    /**
     * Adds a new element to the data structure in its own set.
     *
     * @param element The element to add.
     */
    public void addElement(T element)
    {
        parentMap.put(element, element);
        rankMap.put(element, 0);
    }

    /**
     * @return map from element to parent element
     */
    protected Map<T, T> getParentMap()
    {
        return parentMap;
    }

    /**
     * @return map from element to rank
     */
    protected Map<T, Integer> getRankMap()
    {
        return rankMap;
    }

    /**
     * Returns the representative element of the set that element is in.
     *
     * @param element The element to find.
     *
     * @return The element representing the set the element is in.
     */
    public T find(T element)
    {
        if (!parentMap.containsKey(element)) {
            throw new IllegalArgumentException("elements must be contained in given set");
        }

        T parent = parentMap.get(element);
        if (parent.equals(element)) {
            return element;
        }

        T newParent = find(parent);
        parentMap.put(element, newParent);
        return newParent;
    }

    /**
     * Merges the sets which contain element1 and element2.
     *
     * @param element1 The first element to union.
     * @param element2 The second element to union.
     */
    public void union(T element1, T element2)
    {
        if (!parentMap.containsKey(element1) || !parentMap.containsKey(element2)) {
            throw new IllegalArgumentException("elements must be contained in given set");
        }

        T parent1 = find(element1);
        T parent2 = find(element2);

        // check if the elements are already in the same set
        if (parent1.equals(parent2)) {
            return;
        }

        int rank1 = rankMap.get(parent1);
        int rank2 = rankMap.get(parent2);
        if (rank1 > rank2) {//
            parentMap.put(parent2, parent1);
        } else if (rank1 < rank2) {
            parentMap.put(parent1, parent2);
        } else {
            parentMap.put(parent2, parent1);
            rankMap.put(parent1, rank1 + 1);
        }
    }
}

// End UnionFind.java

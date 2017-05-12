package com.abc.basic.algoritms.base.graph.util;

import java.util.*;

/**
 * A double comparator with adjustable tolerance.
 *
 * @author Dimitrios Michail
 */
public class ToleranceDoubleComparator
        implements Comparator<Double>
{
    /**
     * Default tolerance used by the comparator.
     */
    public static final double DEFAULT_EPSILON = 1e-9;

    private final double epsilon;

    /**
     * Construct a new comparator with a {@link #DEFAULT_EPSILON} tolerance.
     */
    public ToleranceDoubleComparator()
    {
        this(DEFAULT_EPSILON);
    }

    /**
     * Construct a new comparator with a specified tolerance.
     *
     * @param epsilon the tolerance
     */
    public ToleranceDoubleComparator(double epsilon)
    {
        if (epsilon <= 0.0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        this.epsilon = epsilon;
    }

    /**
     * Compares two floating point values. Returns 0 if they are equal, -1 if {@literal o1 < o2}, 1
     * otherwise
     *
     * @param o1 the first value
     * @param o2 the second value
     * @return 0 if they are equal, -1 if {@literal o1 < o2}, 1 otherwise
     */
    @Override
    public int compare(Double o1, Double o2)
    {
        if (Math.abs(o1 - o2) < epsilon) {
            return 0;
        } else {
            return Double.compare(o1, o2);
        }
    }
}

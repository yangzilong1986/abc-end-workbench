package com.abc.algorithms.graph;

import junit.framework.*;

public abstract class EnhancedTestCase
        extends TestCase
{
    // ~ Constructors -----------------------------------------------------------

    /**
     * @see TestCase#TestCase()
     */
    public EnhancedTestCase()
    {
        super();
    }

    /**
     * @see TestCase#TestCase(java.lang.String)
     */
    public EnhancedTestCase(String name)
    {
        super(name);
    }

    // ~ Methods ----------------------------------------------------------------

    /**
     * It means: it's wrong that we got here.
     */
    public void assertFalse()
    {
        assertTrue(false);
    }

    /**
     * It means: it's right that we got here.
     */
    public void assertTrue()
    {
        assertTrue(true);
    }
}


package com.abc.basic.algoritms.base.graph.util;

public class ModifiableInteger
        extends Number
        implements Comparable<ModifiableInteger>
{
    private static final long serialVersionUID = 3618698612851422261L;


    public int value;

    @Deprecated
    public ModifiableInteger()
    {
    }

    public ModifiableInteger(int value)
    {
        this.value = value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    /**
     * Adds one to the value of this modifiable integer.
     */
    public void increment()
    {
        this.value++;
    }

    /**
     * Subtracts one from the value of this modifiable integer.
     */
    public void decrement()
    {
        this.value--;
    }

    @Override
    public int compareTo(ModifiableInteger anotherInteger)
    {
        int thisVal = this.value;
        int anotherVal = anotherInteger.value;

        return (thisVal < anotherVal) ? -1 : ((thisVal == anotherVal) ? 0 : 1);
    }

    /**
     * @see Number#doubleValue()
     */
    @Override
    public double doubleValue()
    {
        return this.value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ModifiableInteger) {
            return this.value == ((ModifiableInteger) o).value;
        }

        return false;
    }

    /**
     * @see Number#floatValue()
     */
    @Override
    public float floatValue()
    {
        return this.value;
    }

    @Override
    public int hashCode()
    {
        return this.value;
    }

    /**
     * @see Number#intValue()
     */
    @Override
    public int intValue()
    {
        return this.value;
    }

    /**
     * @see Number#longValue()
     */
    @Override
    public long longValue()
    {
        return this.value;
    }

    public Integer toInteger()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.value);
    }
}
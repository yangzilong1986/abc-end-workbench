package com.abc.basic.algoritms.matrix;

import com.abc.basic.algoritms.algs4.utils.StdOut;

public class Vector {

    private int d;               // dimension of the vector
    private double[] data;       // array of vector's components


    /**
     * Initializes a d-dimensional zero vector.
     *
     * @param d the dimension of the vector
     */
    public Vector(int d) {
        this.d = d;
        data = new double[d];
    }

    /**
     * Initializes a vector from either an array or a vararg list.
     * The vararg syntax supports a constructor that takes a variable number of
     * arugments such as Vector x = new Vector(1.0, 2.0, 3.0, 4.0).
     *
     * @param a  the array or vararg list
     */
    public Vector(double... a) {
        d = a.length;

        // defensive copy so that client can't alter our copy of data[]
        data = new double[d];
        for (int i = 0; i < d; i++)
            data[i] = a[i];
    }

    public int length() {
        return d;
    }

    public int dimension() {
        return d;
    }

    public double dot(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }
        double sum = 0.0;
        for (int i = 0; i < d; i++)
            sum = sum + (this.data[i] * that.data[i]);
        return sum;
    }


    public double magnitude() {
        return Math.sqrt(this.dot(this));
    }


    public double distanceTo(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }
        return this.minus(that).magnitude();
    }


    public Vector plus(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }
        Vector c = new Vector(d);
        for (int i = 0; i < d; i++)
            c.data[i] = this.data[i] + that.data[i];
        return c;
    }

    public Vector minus(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }
        Vector c = new Vector(d);
        for (int i = 0; i < d; i++) {
            c.data[i] = this.data[i] - that.data[i];
        }
        return c;
    }


    public double cartesian(int i) {
        return data[i];
    }

    @Deprecated
    public Vector times(double alpha) {
        Vector c = new Vector(d);
        for (int i = 0; i < d; i++)
            c.data[i] = alpha * data[i];
        return c;
    }

    public Vector scale(double alpha) {
        Vector c = new Vector(d);
        for (int i = 0; i < d; i++)
            c.data[i] = alpha * data[i];
        return c;
    }

    public Vector direction() {
        if (this.magnitude() == 0.0) {
            throw new ArithmeticException("Zero-vector has no direction");
        }
        return this.times(1.0 / this.magnitude());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < d; i++)
            s.append(data[i] + " ");
        return s.toString();
    }

    /**
     * Unit tests the {@code Vector} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        double[] xdata = { 1.0, 2.0, 3.0, 4.0 };
        double[] ydata = { 5.0, 2.0, 4.0, 1.0 };
        Vector x = new Vector(xdata);
        Vector y = new Vector(ydata);

        StdOut.println("   x       = " + x);
        StdOut.println("   y       = " + y);

        Vector z = x.plus(y);
        StdOut.println("   z       = " + z);

        z = z.times(10.0);
        StdOut.println(" 10z       = " + z);

        StdOut.println("  |x|      = " + x.magnitude());
        StdOut.println(" <x, y>    = " + x.dot(y));
        StdOut.println("dist(x, y) = " + x.distanceTo(y));
        StdOut.println("dir(x)     = " + x.direction());

    }
}
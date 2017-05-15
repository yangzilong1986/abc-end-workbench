package com.abc.basic.algoritms.matrix;

import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.utils.StdOut;

public class DefaultVector implements Cloneable, java.io.Serializable{
    private int d;                   // dimension
    private ST<Integer, Double> st;  // the vector, represented by index-value pairs

   /**
     * Initializes a d-dimensional zero vector.
     * @param d the dimension of the vector
     */
    public DefaultVector(int d) {
        this.d  = d;
        this.st = new ST<Integer, Double>();
    }

    public int getDimension(){
        return d;
    }
    public void put(int i, double value) {
        if (i < 0 || i >= d) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        st.put(i, value);
    }

    public double get(int i) {
        if (i < 0 || i >= d) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        if (st.contains(i)) {
            return st.get(i);
        }
        else                {
            return 0.0;
        }
    }

    public double[] toArray(){
        double[] array=new double[d];
        for (int j=0;j<d;j++){
            double d=st.get(j);
            array[j]=d;
        }
        return array;
    }

    public DefaultVector copy () {

        double[] c =toArray();
        DefaultVector defaultVector=new DefaultVector(this.d);
        for(int j=0;j<c.length;j++){
            defaultVector.put(j,c[j]);
        }
        return new DefaultVector(this.d);
    }

    public Object clone () {
        return this.copy();
    }

    public int nnz() {
        return st.size();
    }

    public int dimension() {
        return d;
    }

    /**
     * 点乘
     * @param that
     * @return
     */
    public double dot(DefaultVector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        double sum = 0.0;

        // iterate over the vector with the fewest nonzeros
        if (this.st.size() <= that.st.size()) {
            for (int i : this.st.keys())
                if (that.st.contains(i)) sum += this.get(i) * that.get(i);
        }
        else  {
            for (int i : that.st.keys())
                if (this.st.contains(i)) sum += this.get(i) * that.get(i);
        }
        return sum;
    }


    public double dot(double[] that) {
        double sum = 0.0;
        for (int i : st.keys())
            sum += that[i] * this.get(i);
        return sum;
    }

    public double magnitude() {
        return Math.sqrt(this.dot(this));
    }

    public double norm() {
        return Math.sqrt(this.dot(this));
    }

    public DefaultVector scale(double alpha) {
        DefaultVector c = new DefaultVector(d);
        for (int i : this.st.keys()) {
            c.put(i, alpha * this.get(i));
        }
        return c;
    }

    public DefaultVector pow(double alpha) {
        DefaultVector c = new DefaultVector(d);
        for (int i : this.st.keys()) {
            c.put(i, Math.pow(this.get(i),alpha));
        }
        return c;
    }

    public DefaultVector  sqrt(){
        DefaultVector c = new DefaultVector(d);
        for (int i : this.st.keys()) {
            c.put(i, Math.sqrt(this.get(i)));
        }
        return c;
    }
    public DefaultVector plus(DefaultVector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        DefaultVector c = new DefaultVector(d);
        for (int i : this.st.keys()) {
            c.put(i, this.get(i));                // c = this
        }
        for (int i : that.st.keys()) {
            c.put(i, that.get(i) + c.get(i));     // c = c + that
        }
        return c;
    }



    public DefaultVector minus(DefaultVector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        DefaultVector c = new DefaultVector(d);
        for (int i : this.st.keys()) {
            c.put(i, this.get(i));                // c = this
        }
        for (int i : that.st.keys()) {
            c.put(i,  c.get(i)- that.get(i));     // c = c + that
        }
        return c;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i : st.keys()) {
            s.append("[" + i + ": " + st.get(i) + "] ");
        }
        return s.toString();
    }


    public static void main(String[] args) {
        DefaultVector a = new DefaultVector(10);
        DefaultVector b = new DefaultVector(10);
        a.put(3, 0.50);
        a.put(9, 0.75);
        a.put(6, 0.11);
        a.put(6, 0.00);
        b.put(3, 0.60);
        b.put(4, 0.90);
        StdOut.println("a = " + a);
        StdOut.println("b = " + b);
        StdOut.println("a dot b = " + a.dot(b));
        StdOut.println("a + b   = " + a.plus(b));
    }

}
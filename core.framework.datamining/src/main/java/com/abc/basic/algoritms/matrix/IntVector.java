package com.abc.basic.algoritms.matrix;


import com.abc.basic.algoritms.algs4.col.ST;

import java.util.Objects;
import java.util.TreeMap;

public class IntVector implements Cloneable, java.io.Serializable {
    private int d;                   // dimension
    private ST<Integer, Integer> st;  // the vector, represented by index-value pairs

    /**
     * Initializes a d-dimensional zero vector.
     * @param d the dimension of the vector
     */
    public IntVector(int d) {
        this.d  = d;
        this.st = new ST<Integer, Integer>();
    }

    public IntVector(int[] data) {
        Objects.requireNonNull(data);
        this.d  = data.length;
        this.st = new ST<Integer, Integer>();
        for(int i=0;i<data.length;i++){
            st.put(i,data[i]);
        }
    }

    public int getDimension(){
        return d;
    }
    public void put(int i, int value) {
        if (i < 0 || i >= d) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        st.put(i, value);
    }

    public int get(int i) {
        if (i < 0 || i >= d) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        if (st.contains(i)) {
            return st.get(i);
        }
        else  {
            return 0;
        }
    }

    public TreeMap<Integer, Integer> sortMin(){
        IntVector defaultVector= (IntVector) this.clone();
        ST<Integer, Integer> sort=new ST<>();
        for(int i:defaultVector.st.keys()){
            sort.put(st.get(i),i);
        }
        return sort.getST();
    }

    public int[] toArray(){
        int[] array=new int[d];
        for (int j=0;j<d;j++){
            int d=st.get(j);
            array[j]=d;
        }
        return array;
    }

    public IntVector copy () {

        int[] c =toArray();
        IntVector defaultVector=new IntVector(this.d);
        for(int j=0;j<c.length;j++){
            defaultVector.put(j,c[j]);
        }
        return defaultVector;
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
    public double dot(IntVector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        int sum = 0;

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


    public double dot(int[] that) {
        int sum = 0;
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

    public IntVector scale(int alpha) {
        IntVector c = new IntVector(d);
        for (int i : this.st.keys()) {
            c.put(i, alpha * this.get(i));
        }
        return c;
    }

    public IntVector pow(int alpha) {
        IntVector c = new IntVector(d);
        for (int i : this.st.keys()) {
            c.put(i, (int) Math.pow(this.get(i),alpha));
        }
        return c;
    }

    public IntVector  sqrt(){
        IntVector c = new IntVector(d);
        for (int i : this.st.keys()) {
            c.put(i, (int) Math.sqrt(this.get(i)));
        }
        return c;
    }
    public IntVector plus(IntVector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        IntVector c = new IntVector(d);
        for (int i : this.st.keys()) {
            c.put(i, this.get(i));                // c = this
        }
        for (int i : that.st.keys()) {
            c.put(i, that.get(i) + c.get(i));     // c = c + that
        }
        return c;
    }



    public IntVector minus(IntVector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        IntVector c = new IntVector(d);
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

}

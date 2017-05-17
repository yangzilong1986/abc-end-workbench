package com.abc.basic.algoritms.matrix;

import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

public class Vector implements Cloneable, java.io.Serializable {
    public TreeMap<Integer, Number> getSt() {
        return st;
    }

    public void setSt(TreeMap<Integer, Number> st) {
        this.st = st;
    }

    private int d;
    private TreeMap<Integer, Number> st;
    private static final int scale=5;
    /**
     * Initializes a d-dimensional zero vector.
     *
     * @param d the dimension of the vector
     */
    public Vector(int d) {
        this.d = d;
        this.st = new TreeMap<Integer, Number>();
    }
    public Vector(int d, double data) {
        Objects.requireNonNull(data);
        this.d = d;
        this.st = new TreeMap<Integer, Number>();
        for (int i = 0; i < d; i++) {
            st.put(i, data);
        }
    }
    public Vector(int d, int data) {
        Objects.requireNonNull(data);
        this.d = d;
        this.st = new TreeMap<Integer, Number>();
        for (int i = 0; i < d; i++) {
            st.put(i, data);
        }
    }
    public Vector(int[] data) {
        Objects.requireNonNull(data);
        this.d = data.length;
        this.st = new TreeMap<Integer, Number>();
        for (int i = 0; i < data.length; i++) {
            st.put(i, data[i]);
        }
    }

    public Vector(Number[] data) {
        Objects.requireNonNull(data);
        this.d = data.length;
        this.st = new TreeMap<Integer, Number>();
        for (int i = 0; i < data.length; i++) {
            st.put(i, data[i]);
        }
    }

    DecimalFormat format;
    //设置某个数的小数部分中所允许的最大数字位数。如需格式化除 BigInteger 和 BigDecimal
    // 对象之外的数字，则使用 newValue 的低位部分和 340。用 0 替换负数输入值。
    private static final int MIND = 0;
    private static final int MAND = 6;

    void format() {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.CHINA));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(MIND);
        format.setMinimumFractionDigits(MAND);
        format.setGroupingUsed(false);
    }

    public int getDimension() {
        return d;
    }

    public void put(int i, Number value) {
        if (i < 0 || i >= d) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        st.put(i, value);
    }

    public Number get(int i) {
        if (i < 0 || i >= d) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        if (st.containsKey(i)) {
            return st.get(i);
        } else {
            return 0.0;
        }
    }

    public TreeMap<Number, Integer> sortMin() {
        Vector defaultVector = (Vector) this.clone();
        TreeMap<Number, Integer> sort = new TreeMap<>();
        for (int i : defaultVector.st.keySet()) {
            sort.put(st.get(i), i);
        }
        return sort;
    }


    public Number[] toArray() {
        Number[] array = new Number[d];
        for (int j = 0; j < d; j++) {
            Number d = st.get(j);
            array[j] = d;
        }
        return array;
    }

    public Vector copy() {

        Number[] c = toArray();
        Vector defaultVector = new Vector(this.d);
        for (int j = 0; j < c.length; j++) {
            defaultVector.put(j, c[j]);
        }
        return defaultVector;
    }

    public Object clone() {
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
     *
     * @param that
     * @return
     */
    public Number dot(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        BigDecimal sum = new BigDecimal(0);

        // iterate over the vector with the fewest nonzeros
        if (this.st.size() <= that.st.size()) {
            for (int i : this.st.keySet()) {
                if (that.st.containsKey(i)) {
                    BigDecimal d = convertNumberToBigDecimal(this.get(i)).multiply(convertNumberToBigDecimal(that.get(i)));
                    sum = sum.add(d);
//                    sum += this.get(i) * that.get(i);
                }
            }
        } else {
            for (int i : that.st.keySet()) {
                if (this.st.containsKey(i)) {
                    BigDecimal d = convertNumberToBigDecimal(this.get(i)).multiply(convertNumberToBigDecimal(that.get(i)));
                    sum = sum.add(d);
//                    sum += this.get(i) * that.get(i);
                }
            }
        }
        return sum;
    }


    public Number dot(Number[] that) {
//        Number sum = 0.0;
        BigDecimal sum = new BigDecimal(0.0);
        for (int i : st.keySet()) {
            BigDecimal d = convertNumberToBigDecimal(that[i]).multiply(convertNumberToBigDecimal(this.get(i)));
            sum = sum.add(d);
//            sum += that[i] * this.get(i);
        }
        return sum;
    }

    public Number magnitude() {
        return Math.sqrt((Double) this.dot(this));
    }

    public Number norm() {
        return Math.sqrt((Double) this.dot(this));
    }

    public Vector scale(Double alpha) {
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            BigDecimal decimal = convertNumberToBigDecimal(this.get(i));
            c.put(i, decimal.multiply(convertNumberToBigDecimal(alpha)));
//            c.put(i, alpha * this.get(i));
        }
        return c;
    }

    public Vector pow(Double alpha) {
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            c.put(i, Math.pow(this.get(i).doubleValue(), alpha));
        }
        return c;
    }


    public Vector pow(Long alpha) {
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            Object l=this.get(i);
            c.put(i, Math.pow(this.get(i).doubleValue(), alpha));
        }
        return c;
    }

    public Vector divide(Double alpha) {
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            BigDecimal d = convertNumberToBigDecimal(this.get(i));
            //除法的小数点数
            BigDecimal dd=d.divide(convertNumberToBigDecimal(alpha),scale,RoundingMode.CEILING);
            c.put(i,dd);
        }
        return c;
    }

    public Vector log() {
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            c.put(i, Math.log(this.get(i).doubleValue()));
        }
        return c;
    }

    public Vector sqrt() {
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            c.put(i, Math.sqrt(this.get(i).doubleValue()));
        }
        return c;
    }

    public Vector plus(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            c.put(i, this.get(i));                // c = this
        }
        for (int i : that.st.keySet()) {
            c.put(i, convertNumberToBigDecimal(c.get(i)).add(convertNumberToBigDecimal(that.get(i))));
//            c.put(i, that.get(i) + c.get(i));     // c = c + that
        }
        return c;
    }

    public Number sum(){
        //        Number sum = 0.0;
        BigDecimal sum = new BigDecimal(0.0);
        for (int i : this.st.keySet()) {
            BigDecimal that=convertNumberToBigDecimal(this.get(i));
            sum=sum.add(that);
        }
        return sum;
    }
    public Vector minus(Vector that) {
        if (this.d != that.d) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        Vector c = new Vector(d);
        for (int i : this.st.keySet()) {
            c.put(i, this.get(i));                // c = this
        }
        for (int i : that.st.keySet()) {
            c.put(i, convertNumberToBigDecimal(c.get(i)).subtract(convertNumberToBigDecimal(that.get(i))));
//            c.put(i,  c.get(i)- that.get(i));
        }
        return c;
    }

    Object convertNumber(Number number) {

        Object decimal = null;
        if (number instanceof Integer) {
            decimal = new BigDecimal((Integer) number);
        } else if (number instanceof Long) {
            decimal = new BigDecimal((Long) number);
        } else if (number instanceof Float) {
            decimal = new BigDecimal((Float) number);
        } else {
            decimal = new BigDecimal((Double) number);
        }
        return decimal;
    }

    BigDecimal convertNumberToBigDecimal(Number number) {

        BigDecimal decimal = null;
        if (number instanceof Integer) {
            decimal = new BigDecimal((Integer) number);
        } else if (number instanceof Long) {
            decimal = new BigDecimal((Long) number);
        } else if (number instanceof Float) {
            decimal = new BigDecimal((Float) number);
        } else  if (number instanceof Double){
            decimal = new BigDecimal((Double) number);
        }
        if(decimal==null){
            return (BigDecimal) number;
        }
        return decimal;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i : st.keySet()) {
            s.append("[" + i + ": " + st.get(i) + "] ");
        }
        return s.toString();
    }


    public static void main(String[] args) {
        Vector a = new Vector(10);
        Vector b = new Vector(10);
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


package com.abc.basic.algoritms.matrix;

import com.abc.basic.algoritms.algs4.utils.StdOut;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

public class Vector <V extends TreeMap<Integer, Number>> implements Cloneable, java.io.Serializable {

    public V getSt() {
        return st;
    }

    public void setSt(V st) {
        this.st = st;
    }

    private int dimension;

    private V st;

    private static final int scale=8;

    private static final MathContext mathContext;
    static {
        mathContext=new MathContext(scale, RoundingMode.HALF_UP);
    }
    //MathContext(int setPrecision, RoundingMode setRoundingMode)

    public Vector() {
    }

    public int getDimension() {
        return dimension;
    }
    public int setDimension() {
        return dimension;
    }

    /**
     * Initializes a d-dimensional zero vector.
     *
     * @param d the dimension of the vector
     */
    public Vector(int d) {
        this.dimension = d;
//        this.st = new TreeMap<Integer, Number>();
        this.st = (V) new TreeMap<Integer, Number>();
    }
    public Vector(int d, double data) {
        Objects.requireNonNull(data);
        this.dimension = d;
//        this.st = new TreeMap<Integer, Number>();
        this.st = (V) new TreeMap<Integer, Number>();
        for (int i = 0; i < d; i++) {
            st.put(i, data);
        }
    }
    public Vector(int d, int data) {
        Objects.requireNonNull(data);
        this.dimension = d;
//        this.st = new TreeMap<Integer, Number>();
        this.st = (V) new TreeMap<Integer, Number>();
        for (int i = 0; i < dimension; i++) {
            st.put(i, data);
        }
    }
    public Vector(int[] data) {
        Objects.requireNonNull(data);
        this.dimension = data.length;
//        this.st = new TreeMap<Integer, Number>();
        this.st = (V) new TreeMap<Integer, Number>();
        for (int i = 0; i < data.length; i++) {
            st.put(i, data[i]);
        }
    }

    public Vector(Number[] data) {
        Objects.requireNonNull(data);
        this.dimension = data.length;
//        this.st = new TreeMap<Integer, Number>();
        this.st = (V) new TreeMap<Integer, Number>();
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



    public void put(int i, Number value) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        st.put(i, value);
    }

    public Number get(int i) {
        if (i < 0 || i >= dimension) {
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
        for (Object i : defaultVector.st.keySet()) {
            sort.put(st.get((Integer)i), (Integer)i);
        }
        return sort;
    }


    public Number[] toArray() {
        Number[] array = new Number[dimension];
        for (int j = 0; j < dimension; j++) {
            Number d = st.get(j);
            array[j] = d;
        }
        return array;
    }

    public Vector copy() {

        Number[] c = toArray();
        Vector defaultVector = new Vector(this.dimension);
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

    /**
     * 点乘
     * 标量积，在数学中，数量积（dot product; scalar product，也称为点积）是接受在实数R上的两个向量
     * 并返回一个实数值标量的二元运算。它是欧几里得空间的标准内积。
     * @param that
     * @return
     */
    public Number dot(Vector that) {
        if (this.dimension != that.dimension) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        BigDecimal sum = new BigDecimal(0);

        // iterate over the vector with the fewest nonzeros
        if (this.st.size() <= that.st.size()) {
            for (int i : this.st.keySet()) {
                if (that.st.containsKey(i)) {
                    BigDecimal d = convertNumberToBigDecimal(this.get(i)).multiply(convertNumberToBigDecimal(that.get(i)),mathContext);
                    sum = sum.add(d);
//                    sum += this.get(i) * that.get(i);
                }
            }
        } else {
            for (Object i : that.st.keySet()) {
                if (this.st.containsKey(i)) {
                    BigDecimal d = convertNumberToBigDecimal(this.get((Integer)i)).multiply(convertNumberToBigDecimal(that.get((Integer)i)),mathContext);
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
            BigDecimal d = convertNumberToBigDecimal(that[i]).multiply(convertNumberToBigDecimal(this.get(i)),mathContext);
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

    /**
     * 乘法
     * @param alpha
     * @return
     */
    public Vector scale(Double alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            BigDecimal decimal = convertNumberToBigDecimal(this.get(i));
            c.put(i, decimal.multiply(convertNumberToBigDecimal(alpha),mathContext));
        }
        return c;
    }

    /**
     * 数乘法
     * @param that
     * @return
     */
    public Vector scale(Vector that) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            BigDecimal decimal = convertNumberToBigDecimal(this.get(i));
            BigDecimal alpha= convertNumberToBigDecimal(that.get(i));
            c.put(i, decimal.multiply(convertNumberToBigDecimal(alpha),mathContext));
//            c.put(i, alpha * this.get(i));
        }
        return c;
    }

    public Vector pow(Double alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            c.put(i, Math.pow(this.get(i).doubleValue(), alpha));
        }
        return c;
    }


    public Vector pow(Long alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            Object l=this.get(i);
            c.put(i, Math.pow(this.get(i).doubleValue(), alpha));
        }
        return c;
    }

    public Vector exp() {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            Object l=this.get(i);
            c.put(i, Math.exp(this.get(i).doubleValue()));
        }
        return c;
    }

    public Vector negate() {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            Object l=this.get(i);
            BigDecimal bigDecimal=convertNumberToBigDecimal(this.get(i));
            c.put(i, bigDecimal.negate());
        }
        return c;
    }


    public Vector divide(Double alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            BigDecimal d = convertNumberToBigDecimal(this.get(i));
            //除法的小数点数
            BigDecimal dd=d.divide(convertNumberToBigDecimal(alpha),scale,RoundingMode.CEILING);
            c.put(i,dd);
        }
        return c;
    }


    /**
     * 向量在分母
     * @param alpha
     * @return
     */
    public Vector divideThis(Double alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            BigDecimal d = convertNumberToBigDecimal(this.get(i));
            BigDecimal alphaB= convertNumberToBigDecimal(alpha);
            //除法的小数点数
            BigDecimal dd=alphaB.divide(d,scale,RoundingMode.CEILING);
            c.put(i,dd);
        }
        return c;
    }

    public Vector add(Double alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            BigDecimal d = convertNumberToBigDecimal(this.get(i));
            //除法的小数点数
            BigDecimal dd=d.add(convertNumberToBigDecimal(alpha));
            c.put(i,dd);
        }
        return c;
    }

    public Vector add(Long alpha) {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            BigDecimal d = convertNumberToBigDecimal(this.get(i));
            //除法的小数点数
            BigDecimal dd=d.add(convertNumberToBigDecimal(alpha));
            c.put(i,dd);
        }
        return c;
    }

    public Vector log() {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            c.put(i, Math.log(this.get(i).doubleValue()));
        }
        return c;
    }

    public Vector sqrt() {
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            c.put(i, Math.sqrt(this.get(i).doubleValue()));
        }
        return c;
    }

    public Vector plus(Vector that) {
        if (this.dimension != that.dimension) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            c.put(i, this.get(i));                // c = this
        }
        for (Object i : that.st.keySet()) {
            c.put((Integer)i, convertNumberToBigDecimal(c.get((Integer)i)).add(convertNumberToBigDecimal(that.get((Integer)i)),mathContext));
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

    /**
     * 减法，this-that
     * @param that
     * @return
     */
    public Vector minus(Vector that) {
        if (this.dimension != that.dimension) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        Vector c = new Vector(dimension);
        for (int i : this.st.keySet()) {
            c.put(i, this.get(i));                // c = this
        }
        for (Object i : that.st.keySet()) {
            c.put((Integer)i, convertNumberToBigDecimal(c.get((Integer)i)).subtract(convertNumberToBigDecimal(that.get((Integer)i))));
//            c.put(i,  c.get(i)- that.get(i));
        }
        return c;
    }

    Object convertNumber(Number number) {
        //BigDecimal(BigInteger unscaledVal, int scale, MathContext mc)
        Object decimal = null;
        if (number instanceof Integer) {
            decimal = new BigDecimal((Integer) number,mathContext);
        } else if (number instanceof Long) {
            decimal = new BigDecimal((Long) number,mathContext);
        } else if (number instanceof Float) {
            decimal = new BigDecimal((Float) number,mathContext);
        } else {
            decimal = new BigDecimal((Double) number,mathContext);
        }
        return decimal;
    }

    BigDecimal convertNumberToBigDecimal(Number number) {

        BigDecimal decimal = null;
        if (number instanceof Integer) {
            decimal = new BigDecimal((Integer) number,mathContext);
        } else if (number instanceof Long) {
            decimal = new BigDecimal((Long) number,mathContext);
        } else if (number instanceof Float) {
            decimal = new BigDecimal((Float) number,mathContext);
        } else  if (number instanceof Double){
            try {
                decimal = new BigDecimal((Double) number,mathContext);
            }catch (NumberFormatException e){
                decimal = new BigDecimal(0.0,mathContext);
            }
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


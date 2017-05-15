package com.abc.basic.algoritms.matrix;

import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.TreeMap;

public class DefaultMatrix<V extends DefaultVector> implements Cloneable, java.io.Serializable{

    protected DefaultMatrix matrixT;
    protected ST<Integer, V> matrix;
    protected int row;
    protected int col;

    ////////////////////构造方法//////////////////////////
    public DefaultMatrix(double[] data, int row, int col) {
        this(col);
        if (data==null||data.length==0||col==0||row==0) {
            throw new IllegalArgumentException("列长度不能为0");
        }
        if(data.length!=row*col){
            throw new IllegalArgumentException("行和列与给出数据不一致");
        }
        addVector(data);
    }

    public DefaultMatrix (double vals[], int m) {// row,//m 行
        this.col = (m != 0 ? vals.length/m : 0);
        if (m*col != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        double[][] A = new double[m][this.col];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < this.col; j++) {
                A[i][j] = vals[i+j*m];
            }
        }
        createDefaultMatrix(A,new Shape(1,1));
    }
    /**
     * 初始化矩阵为N列
     * @param col
     */
    public DefaultMatrix(int col) {
        if (col==0) {
            throw new IllegalArgumentException("列长度不能为0");
        }
        this.col = col;
        matrix = new ST<Integer, V>();
    }

    /**
     * 矩阵初始化为每个值都是data
     * @param row
     * @param col
     * @param data
     */
    public DefaultMatrix(int row,int col,double data) {
        this(col);
        double[][] d=new double[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                d[i][j] = data;
            }
        }
        addVector(data);
    }

    public DefaultMatrix (double[][] vals){
       this(vals,new Shape(1,1));

    }
    /**
     * 行列上按照shape把数组扩充，这里的数组为一维数组
     * @param vals
     * @param shape
     */
    public DefaultMatrix (double[] vals, Shape shape){
        this (vals, Axis.col, 1);
        double[] tmp=this.getVectorToArray(row-1);
        for(int i=0;i<shape.row;i++){
            this.addSingleVector(tmp);
        }
    }

    public DefaultMatrix (double[] vals){
        this (vals, Axis.col, 1);
    }

    public DefaultMatrix (double[][] vals, Shape shape){
        createDefaultMatrix(vals,shape);

    }

    private void createDefaultMatrix (double[][] vals, Shape shape){
        if (vals==null||vals.length==0||vals[0]==null||vals[0].length==0||shape==null) {
            throw new IllegalArgumentException("数组必须是二维数组，形状必须定义");
        }
//        int row=vals[0].length*shape.row;
        int row=vals.length*shape.row;
        this.col =vals[0].length*shape.col;
        matrix = new ST<Integer, V>();


        for(int i=0;i<row;i++) {
            V vector=this.createDefaultVector(this.col);
            double[] datas=new double[this.col];
            for (int j = 0; j < col; j++) {
                datas[j] = vals[i/shape.row][j/shape.col];
            }
            addVector(vector,datas);
        }

    }
    /**
     * 按照数组在行或者列的方向上扩充s倍
     * @param vals 初始化数据
     * @param axis 行或者列方向
     * @param s 扩充倍数
     */
    public DefaultMatrix (double[] vals, Axis axis, int s) {
        if (vals == null) {
            throw new IllegalArgumentException("矩阵数据不能为空");
        }
        matrix = new ST<Integer, V>();
        switch (axis){
            case row:
                this.col=vals.length;
                for(int i=0;i<s;i++ ) {
                    addVector(vals);
                }
                break;
            case col:
            default:
                this.col =vals.length*s;
                double[] datas=new double[col];
                for (int i=0;i<col;i++){
                    datas[i]=vals[i/vals.length];
                }
                V vector=createDefaultVector(col);
                addVector(vector,datas);


        }

    }
    ////////////////////添加矩阵数据//////////////////////////
    public void addVector(V vector) {
        this.matrix.put(this.row++, vector);
        ;
    }

    public void addVector(double[][] data) {
        if (data == null || data[0].length != this.col) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        for (int i = 0; i < data.length; i++) {
            addVector(data[i]);
        }
    }


    public void addVector(V vector,double[] data) {
        if (data == null || data.length != this.col) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        for (int i = 0; i < data.length; i++) {
            vector.put(i, data[i]);
        }

        this.matrix.put(this.row++, (V) vector);
        ;
    }

    public void addSingleVector(double[] data) {
        if (data == null || data.length != this.col) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        V vector = createDefaultVector(this.col);
        addVector(vector,data);
    }

    public V createDefaultVector(int dimension){
        return (V)new DefaultVector(dimension);
    }

    public void addVector(double... a) {
        int d = a.length;
        double[] data = a;
        if (data == null || data.length % this.col != 0) {
            throw new IllegalArgumentException("数组长度不是列的长度");
        }
        for (int i = 0; i < data.length/this.col; i++) {
            double[] single=new double[this.col];
            System.arraycopy(data,i*col,single,0,this.col);
            addSingleVector(single);
        }
    }

    public DefaultMatrix copy () {

        double[][] c =toArray();
        return new DefaultMatrix<V>(c);
    }

    public Object clone () {
        return this.copy();
    }


    /**
     *
     * @return
     */
    public V getVector(int row){
        return this.matrix.get(row);
    }
    public double[] getVectorToArray(int row){
        return this.matrix.get(row).toArray();
    }
    ////////////////////矩阵转换为数组//////////////////////////
    public double[][] toArray(){
        double[][] array=new double[row][col];
        for(int i=0;i<row;i++){
            DefaultVector vector=matrix.get(i);
            for (int j=0;j<col;j++){
                Double d=vector.get(j);
                array[i][j]=d;
            }
        }
        return array;
    }

    /**
     * 逆矩阵缓存
     * @return
     */
    public DefaultMatrix transposeC() {
        if(matrixT==null || matrixT.col!=this.row||matrixT.row!=this.col){
            transpose();
        }
        return matrixT;
    }

    /**
     * 直接求逆矩阵
     * @return
     */
    public DefaultMatrix transpose() {
        matrixT = new DefaultMatrix(row);
        double[][] C = toArray();
        double[][] array=new double[col][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                array[j][i] = C[i][j];
            }
        }
        matrixT.addVector(array);
        return matrixT;
    }

    public DefaultMatrix times (double alpha) {
        DefaultMatrix tmpMatrix = new DefaultMatrix(this.col);
        for (int i : matrix.keys()) {
            DefaultVector vector = matrix.get(i);
            DefaultVector temp=vector.scale(alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public DefaultMatrix sqrt () {
        DefaultMatrix tmpMatrix = new DefaultMatrix(this.col);
        for (int i : matrix.keys()) {
            DefaultVector vector = matrix.get(i);
            DefaultVector temp=vector.sqrt();
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public DefaultMatrix pow (double alpha) {
        DefaultMatrix tmpMatrix = new DefaultMatrix(this.col);
        for (int i : matrix.keys()) {
            DefaultVector vector = matrix.get(i);
            DefaultVector temp=vector.pow(alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public DefaultMatrix divid (double alpha) {
       return times(1/alpha);
    }

    /**
     * this为被减数
     * @param that 减数
     * @return
     */
    public DefaultMatrix minus (DefaultMatrix that) {
        checkMatrixDimensions(that);
        DefaultMatrix tmpMatrix = new DefaultMatrix(this.col);
        for (int i : matrix.keys()) {
            DefaultVector vector = matrix.get(i);
            DefaultVector temp=vector.minus(that.getVector(i));
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public DefaultMatrix minus () {
        DefaultMatrix tmpMatrix = new DefaultMatrix(this.col);
        DefaultVector vector = (DefaultVector) matrix.get(0).clone();
        for (int i : matrix.keys()) {
           if(i<=1){
               continue;
           }
            DefaultVector next = (DefaultVector) matrix.get(i).clone();
            vector=vector.minus(next);
        }
        return tmpMatrix;
    }


    /**
     *a = np.array([
     *     [1,2,3],
     *     [4,5,6]
     * ])
     * print(a.sum())           # 对整个矩阵求和
     *  结果 21
     *
     * print(a.sum(axis=0)) # 对行方向求和
     * 结果 [5 7 9]
     *
     * print(a.sum(axis=1)) # 对列方向求和
     * 结果 [ 6 15]
     * @param axis
     * @return
     */
    public DefaultMatrix plus (Axis axis) {

        switch (axis){
            case row:
                return plus (this);
            case col:
            default:
                DefaultMatrix tmpMatrix=this.transposeC();
                return plus(tmpMatrix);
        }
    }

    public DefaultMatrix plus (DefaultMatrix defaultMatrix) {
        DefaultMatrix tmpMatrix = new DefaultMatrix(defaultMatrix.col);
        DefaultVector vector = (DefaultVector) defaultMatrix.matrix.get(0);
        for (Object i : defaultMatrix.matrix.keys()) {
            if((Integer)i <1){
                continue;
            }
            DefaultVector next = (DefaultVector) defaultMatrix.matrix.get((Integer)i);
            vector=vector.plus(next);
        }
        tmpMatrix.addVector(vector);
        return tmpMatrix;
    }

    public DefaultMatrix plus () {
        DefaultMatrix temp=new DefaultMatrix(1);
        DefaultMatrix row=plus(this);
        DefaultVector defaultVector=row.getVector(0);
        double sum=0.0;
        for (int i = 0; i < defaultVector.getDimension(); i++) {
            sum+=defaultVector.get(i);
        }
        V tempVector= (V) new  DefaultVector(1);
        tempVector.put(0,sum);
        temp.addVector(tempVector);
        return temp;
    }


    private void checkMatrixDimensions (DefaultMatrix B) {
        if (B.row!= this.row || B.col != this.col) {
            throw new IllegalArgumentException("矩阵的维度必须一致(Matrix dimensions must agree.)");
        }
    }

    public Shape getShape(){
        return new Shape(this.row,this.col);
    }
    /**
     * sortVectorByKey
     */
    public TreeMap sortVectorByKey(int row){
        DefaultVector vector=this.matrix.get(row);
        return vector.sortMin();
    }
    /**
     * 返回形状，即多少列多少行
     */
    public Shape shape(){
        return new Shape(row,col);
    }

    public static enum Axis{
        row,//m 行
        col//n 列
    }

    public static class Shape{
        public final int row;
        public final int col;

        public Shape(int row,int col){
            this.row=row;
            this.col=col;
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i : matrix.keys()) {
            s.append("[" + i + "-> " + matrix.get(i) + "] ");
        }
        return s.toString();
    }

    public static void main(String[] args) {

        double[] inX = {0, 0};
//        double[][] vals = {{1.1, 1.2}, {2.1, 2.2}, {0., 0.}, {3.1, 3.2}};
        double[][] vals = {{ 1., 1.1},{1. ,  1.},{0. ,  0. },{0.,  0.1}};
        DefaultMatrix dataMatrix=new DefaultMatrix(vals);

        StdOut.println("dataMatrix = " + dataMatrix);

        DefaultMatrix inXMatrix = new DefaultMatrix(inX,Axis.row,vals.length);//两列

        StdOut.println("inXMatrix = " + inXMatrix);
        DefaultMatrix minusMatrix=inXMatrix.minus(dataMatrix);
        StdOut.println("minusMatrix = " + minusMatrix);

        DefaultMatrix posMatrix=minusMatrix.pow(2);
        StdOut.println("posMatrix = " + posMatrix);

        DefaultMatrix plusMatrix=posMatrix.plus(Axis.col);
        StdOut.println("plusMatrix = " + plusMatrix);

        DefaultMatrix sqrtMatrix =plusMatrix.sqrt();

        StdOut.println("sqrtMatrix = " + sqrtMatrix);

        TreeMap mapSort=sqrtMatrix.sortVectorByKey(0);


        StdOut.println("map = " + mapSort);



    }

    public static void testComputer(){
        /**
         *a = np.array([
         *     [1,2,3],
         *     [4,5,6]
         * ])
         * print(a.sum())           # 对整个矩阵求和
         *  结果 21
         *
         * print(a.sum(axis=0)) # 对行方向求和
         * 结果 [5 7 9]
         **/
        double[][] sumData = {{ 1,2,3},{4,5,6}};
        DefaultMatrix sumMatrix=new DefaultMatrix(sumData);
        StdOut.println("sumMatrix = " + sumMatrix);

        DefaultMatrix sumResult=sumMatrix.plus(Axis.row);
        StdOut.println("\nsumResult = " + sumResult);

        DefaultMatrix sumResultCol=sumMatrix.plus(Axis.col);
        StdOut.println("\nsumResultCol = " + sumResultCol);

        DefaultMatrix sumResultDefaultl=sumMatrix.plus();
        StdOut.println("\nsumResultDefaultl = " + sumResultDefaultl);
    }
    public static void testDefaultMatrix() {
        DefaultMatrix a = new DefaultMatrix(2);
        double[] d = {1, 2};
        a.addVector(d);
        double[][] vals = {{1., 1.1}, {1., 1.}, {0., 0.}, {0., 0.1}};
        a.addVector(vals);
        StdOut.println("a = " + a);
        double[] val = {1., 1.1, 1., 1., 0., 0., 0., 0.1};
        a.addVector(val);
        a.addVector(1,3,4,5);
        double[][] array=a.toArray();

        DefaultMatrix aT=a.transposeC();

        double[] inX={3,0};
        DefaultMatrix dd=new DefaultMatrix(inX,Axis.row,6);
        StdOut.println("dd = " + dd);

        DefaultMatrix mShape=new DefaultMatrix(vals,new Shape(2,2));
        StdOut.println("mShape = " + mShape);
    }
}

package com.abc.basic.algoritms.matrix;


import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.utils.StdOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.TreeMap;

public class Matrix<V extends Vector> implements Cloneable, java.io.Serializable {
    private static final Logger log = LoggerFactory.getLogger(Matrix.class);
    protected Matrix matrixT;
    protected ST<Integer, V> matrix;
    protected int row;
    protected int col;

    public ST<Integer, V> getMatrix() {
        return matrix;
    }

    public void setMatrix(ST<Integer, V> matrix) {
        this.matrix = matrix;
    }

    ////////////////////构造方法//////////////////////////
    public Matrix(Integer[] data, int row, int col){
        this(col);
        initMatrix(data, row, col);
    }
    public Matrix(Double[] data, int row, int col){
        this(col);
        initMatrix(data, row, col);
    }
    public Matrix(Float[] data, int row, int col){
        this(col);
        initMatrix(data, row, col);
    }
    public Matrix(Long[] data, int row, int col){
        this(col);
        initMatrix(data, row, col);
    }

    public Matrix (Double vals[], int m){
        initMatrix (vals, m);
    }

    public Matrix (Float vals[], int m){
        initMatrix (vals, m);
    }

    public Matrix (Long vals[], int m){
        initMatrix (vals, m);
    }

    public Matrix (Integer vals[], int m){
        initMatrix (vals, m);
    }

    public void initMatrix(Number[] data, int row, int col) {

        if (data==null||data.length==0||col==0||row==0) {
            throw new IllegalArgumentException("列长度不能为0");
        }
        if(data.length!=row*col){
            throw new IllegalArgumentException("行和列与给出数据不一致");
        }
        addVector(data);
    }

    public void initMatrix (Number vals[], int m) {// row,//m 行
        this.col = (m != 0 ? vals.length/m : 0);
        if (m*col != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        Number[][] A = new Number[m][this.col];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < this.col; j++) {
                A[i][j] = vals[i+j*m];
            }
        }
        createDefaultMatrix(A,new Matrix.Shape(1,1));
    }
    /**
     * 初始化矩阵为N列
     * @param col
     */
    public Matrix(int col) {
        if (col==0) {
            throw new IllegalArgumentException("列长度不能为0");
        }
        this.col = col;
        matrix = new ST<Integer, V>();
    }

    public Matrix(int row,int col,Long data){
        this(col);
        initMatrix(row,col, data);
    }
    public Matrix(int row,int col,Integer data){
        this(col);
        initMatrix(row,col, data);
    }
    public Matrix(int row,int col,Float data){
        this(col);
        initMatrix(row,col, data);
    }
    public Matrix(int row,int col,Double data){
        this(col);
        initMatrix(row,col, data);
    }
    /**
     * 矩阵初始化为每个值都是data
     * @param row
     * @param col
     * @param data
     */
    public void initMatrix(int row,int col,Number data) {

        Number[][] d=new Number[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                d[i][j] = data;
            }
        }
        addVector(d);
    }

    public Matrix (Double[][] vals){
        this(vals,new Matrix.Shape(1,1));
    }

    public Matrix (Integer[][] vals){
        this(vals,new Matrix.Shape(1,1));
    }
    public Matrix (Float[][] vals){
        this(vals,new Matrix.Shape(1,1));
    }

    public Matrix (Long[][] vals){
        this(vals,new Matrix.Shape(1,1));
    }

    /**
     * 行列上按照shape把数组扩充，这里的数组为一维数组
     * @param vals
     * @param shape
     */
    public Matrix (Double[] vals, Matrix.Shape shape){
        this (vals, Matrix.Axis.col, 1);
        Number[] tmp=this.getVectorToArray(row-1);
        for(int i=0;i<shape.row;i++){
            this.addSingleVector(tmp);
        }
    }

    public Matrix (Integer[] vals, Matrix.Shape shape){
        this (vals, Matrix.Axis.col, 1);
        Number[] tmp=this.getVectorToArray(row-1);
        for(int i=0;i<shape.row;i++){
            this.addSingleVector(tmp);
        }
    }

    public Matrix (Long[] vals){
        this (vals, Matrix.Axis.col, 1);
    }

    public Matrix (Double[] vals){
        this (vals, Matrix.Axis.col, 1);
    }

    public Matrix (Integer[] vals){
        this (vals, Matrix.Axis.col, 1);
    }
    public Matrix (Float[] vals){
        this (vals, Matrix.Axis.col, 1);
    }

    public Matrix (Long[][] vals, Matrix.Shape shape){
        createDefaultMatrix(vals,shape);
    }
    public Matrix (Integer[][] vals, Matrix.Shape shape){
        createDefaultMatrix(vals,shape);
    }
    public Matrix (Float[][] vals, Matrix.Shape shape){
        createDefaultMatrix(vals,shape);
    }
    public Matrix (Double[][] vals, Matrix.Shape shape){
        createDefaultMatrix(vals,shape);
    }
//    public Matrix (Number[][] vals, Matrix.Shape shape){
//        createDefaultMatrix(vals,shape);
//    }

    private void createDefaultMatrix (Number[][] vals, Matrix.Shape shape){
        if (vals==null||vals.length==0||vals[0]==null||vals[0].length==0||shape==null) {
            throw new IllegalArgumentException("数组必须是二维数组，形状必须定义");
        }
//        int row=vals[0].length*shape.row;
        int row=vals.length*shape.row;
        this.col =vals[0].length*shape.col;
        matrix = new ST<Integer, V>();


        for(int i=0;i<row;i++) {
            V vector=this.createDefaultVector(this.col);
            Number[] datas=new Number[this.col];
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
    public Matrix (Number[] vals, Matrix.Axis axis, int s) {
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
                Number[] datas=new Number[col];
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

    public void addVector(Number[][] data) {
        if (data == null || data[0].length != this.col) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        for (int i = 0; i < data.length; i++) {
            addVector(data[i]);
        }
    }


    public void addVector(V vector,Number[] data) {
        if (data == null || data.length != this.col) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        for (int i = 0; i < data.length; i++) {
            vector.put(i, data[i]);
        }

        this.matrix.put(this.row++, (V) vector);
        ;
    }

    public void addSingleVector(Number[] data) {
        if (data == null || data.length != this.col) {
            throw new IllegalArgumentException("Vector lengths disagree");
        }
        V vector = createDefaultVector(this.col);
        addVector(vector,data);
    }

    public V createDefaultVector(int dimension){
        return (V)new Vector(dimension);
    }

    public void addVector(Number... a) {
        int d = a.length;
        Number[] data = a;
        if (data == null || data.length % this.col != 0) {
            throw new IllegalArgumentException("数组长度不是列的长度");
        }
        for (int i = 0; i < data.length/this.col; i++) {
            Number[] single=new Number[this.col];
            System.arraycopy(data,i*col,single,0,this.col);
            addSingleVector(single);
        }
    }

    public Matrix copy () {

        Double[][] c = (Double[][]) toArray();
        return new Matrix<V>(c);
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
    public Number[] getVectorToArray(int row){
        return this.matrix.get(row).toArray();
    }
    ////////////////////矩阵转换为数组//////////////////////////
    public Number[][] toArray(){
        Number[][] array=new Number[row][col];
        for(int i=0;i<row;i++){
            Vector vector=matrix.get(i);
            for (int j=0;j<col;j++){
                Number d= (Number) vector.get(j);
                array[i][j]=d;
            }
        }
        return array;
    }

    /**
     * 逆矩阵缓存
     * @return
     */
    public Matrix transposeC() {
        if(matrixT==null || matrixT.col!=this.row||matrixT.row!=this.col){
            transpose();
        }
        return matrixT;
    }

    /**
     * 直接求逆矩阵
     * @return
     */
    public Matrix transpose() {
        matrixT = new Matrix(row);
        Number[][] C = toArray();
        Number[][] array=new Number[col][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                array[j][i] = C[i][j];
            }
        }
        matrixT.addVector(array);
        return matrixT;
    }

    /**
     * 矩阵乘一个标量
     * @param alpha
     * @return
     */
    public Matrix times (Number alpha) {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.scale((Double) alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    /**
     *
     */
    /**
     * 矩阵内的数据做普通乘法
     * [1,2,3]   [1,2,3]   [1 4  9]
     * [4,5,6]   [4,5,6]   [16,25,36]
     * [7,8,9]   [7,8,9]   [49,64,81]
     * @param that
     * @return
     */
    public Matrix timesMultiply (Matrix that) {
        if(this.col!=that.col||this.row!=that.row){
            throw new IllegalArgumentException("Matrix Cmn mn*Bmn ");
        }

        Matrix tmpMatrix = new Matrix(that.col);
        for(int j=0;j<row;j++) {//生成矩阵的行，this
            Vector aVector = matrix.get(j);
            Vector bVector = (Vector) that.matrix.get((Integer)j);
            Vector cVector=aVector.scale(bVector);

            tmpMatrix.addVector(cVector);
        }
        return tmpMatrix;
    }
    /**
     * 矩阵内的数据做普通乘法
     * [1,2,3]   [1,2,3]   [1 4  9]
     * [4,5,6]             [4,10,18]
     * [7,8,9]             [7,16,27]
     * @param bVector
     * @return
     */
    public Matrix timesMultiply (Vector bVector) {
        if(this.col!=bVector.getDimension()){
            throw new IllegalArgumentException("Matrix Cmn mn*Bn ");
        }

        Matrix tmpMatrix = new Matrix(this.col);
        for(int j=0;j<row;j++) {//生成矩阵的行，this
            Vector aVector = matrix.get(j);
            Vector cVector=aVector.scale(bVector);

            tmpMatrix.addVector(cVector);
        }
        return tmpMatrix;
    }
    /**
     * 矩阵内的数据以列为基准做点积
     * [1,2,3]   [1,2,3]   [14]
     * [4,5,6]   [4,5,6]   [67]
     * [7,8,9]   [7,8,9]   [194]
     * @param that
     * @return
     */
    public Vector timesDot (Matrix that) {
        if(this.col!=that.col||this.row!=that.row){
            throw new IllegalArgumentException("Matrix Cmn mn*Bmn ");
        }

//        Matrix tmpMatrix = new Matrix(that.col);
        Vector cVector=new Vector(this.col);
        for(int j=0;j<row;j++) {//生成矩阵的行，this
            Vector aVector = matrix.get(j);
            Vector bVector = (Vector) that.matrix.get((Integer)j);
            cVector.put(j,aVector.dot(bVector));

        }
//        tmpMatrix.addVector(cVector);
        return cVector;
    }

    /**
     * 矩阵内的数据以列为基准做点积
     * [1,2,3]   [1,2,3]   [14]
     * [4,5,6]             [32]
     * [7,8,9]             [50]
     * @param bVector
     * @return
     */
    public Vector timesDot ( Vector bVector) {
        if(this.col!=bVector.getDimension()){
            throw new IllegalArgumentException("Matrix Cmn mn*Bn ");
        }

//        Matrix tmpMatrix = new Matrix(this.col);
        Vector cVector=new Vector(this.col);
        for(int j=0;j<row;j++) {//生成矩阵的行，this
            Vector aVector = matrix.get(j);
            cVector.put(j,aVector.dot(bVector));

        }
//        tmpMatrix.addVector(cVector);
        return cVector;
    }

    /**
     * 矩阵乘法，this*that
     * @param that
     * @return
     */
    public Matrix times (Matrix that) {
        if(this.col!=that.row){
            throw new IllegalArgumentException("Matrix Cji Ajm*Bmi ");
        }
        //
        //结果矩阵
        //结果矩阵的列数和that矩阵一致
        Matrix tmpMatrix = new Matrix(that.col);
        //
        Matrix thatT=that.transposeC();
        for(int j=0;j<row;j++) {//生成矩阵的行，this
            Vector aVector = matrix.get(j);
            Vector cVector=new Vector(that.col);
            for (Object i : thatT.matrix.keys()) {//that
                Vector bVector = (Vector) thatT.matrix.get((Integer)i);
                Number number=aVector.dot(bVector);
                if(log.isDebugEnabled()){
                    log.debug("行 j:"+j);
                    log.debug("列 i:"+i);
                    log.debug("结果:"+number);
                }
                cVector.put((Integer)i,number);
            }
            tmpMatrix.addVector(cVector);
        }
        return tmpMatrix;
    }


    /**
     * 矩阵加
     * @param that
     * @return
     */
    public Matrix plusThat (Matrix that) {
        if(this.col!=that.col){
            throw new IllegalArgumentException("Matrix Cji Ajm*Bmi ");
        }
        Matrix tmpMatrix = new Matrix(that.col);
        for(int j=0;j<row;j++) {//生成矩阵的行，this
            Vector aVector = matrix.get(j);
            Vector bVector = (Vector) that.matrix.get((Integer)j);
            Vector cVector=aVector.plus(bVector);

            tmpMatrix.addVector(cVector);
        }
        return tmpMatrix;
    }

    /**
     * 向量sqrt
     * @return
     */
    public Matrix sqrt () {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.sqrt();
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix pow (Double alpha) {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.pow((Double) alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix pow (Long alpha) {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.pow((Long) alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix add (Long alpha) {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.add((Long) alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix add (Double alpha) {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.add((Double) alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix exp (){
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.exp();
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix negate (){
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.negate();
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix divid (Double alpha) {
        return times(1/alpha);
    }

    public Matrix divid (Integer alpha) {
        return times(1/alpha);
    }

    /**
     * 向量在分母
     * @param alpha
     * @return
     */
    public Matrix divideThis (Double alpha) {
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.divideThis((Double) alpha);
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }
    /**
     * this为被减数， this-that
     * @param that 减数
     * @return
     */
    public Matrix minus (Matrix that) {
        checkMatrixDimensions(that);
        Matrix tmpMatrix = new Matrix(this.col);
        for (int i : matrix.keys()) {
            Vector vector = matrix.get(i);
            Vector temp=vector.minus(that.getVector(i));
            tmpMatrix.addVector(temp);
        }
        return tmpMatrix;
    }

    public Matrix minus () {
        Matrix tmpMatrix = new Matrix(this.col);
        Vector vector = (Vector) matrix.get(0).clone();
        for (int i : matrix.keys()) {
            if(i<=1){
                continue;
            }
            Vector next = (Vector) matrix.get(i).clone();
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
    public Matrix plus (Matrix.Axis axis) {

        switch (axis){
            case row:
                return plus (this);
            case col:
            default:
                Matrix tmpMatrix=this.transposeC();
                return plus(tmpMatrix);
        }
    }

    /**
     *
     * @param defaultMatrix
     * @return
     */
    public Matrix plus (Matrix defaultMatrix) {
        Matrix tmpMatrix = new Matrix(defaultMatrix.col);
        Vector vector = (Vector) defaultMatrix.matrix.get(0);
        for (Object i : defaultMatrix.matrix.keys()) {
            if((Integer)i <1){
                continue;
            }
            Vector next = (Vector) defaultMatrix.matrix.get((Integer)i);
            vector=vector.plus(next);
        }
        tmpMatrix.addVector(vector);
        return tmpMatrix;
    }

    public Matrix plus () {
        Matrix temp=new Matrix(1);
        Matrix row=plus(this);
        Vector defaultVector=row.getVector(0);
        BigDecimal sum=new BigDecimal(0);
        for (int i = 0; i < defaultVector.getDimension(); i++) {
            BigDecimal d = defaultVector.convertNumberToBigDecimal(defaultVector.get(i));
            sum = sum.add(d);
//            sum+=defaultVector.get(i);
        }
        V tempVector= (V) new Vector(1);
        tempVector.put(0,sum);
        temp.addVector(tempVector);
        return temp;
    }


    private void checkMatrixDimensions (Matrix B) {
        if (B.row!= this.row || B.col != this.col) {
            throw new IllegalArgumentException("矩阵的维度必须一致(Matrix dimensions must agree.)");
        }
    }

    public Matrix.Shape getShape(){
        return new Matrix.Shape(this.row,this.col);
    }
    /**
     * sortVectorByKey
     */
    public TreeMap sortVectorByKey(int row){
        Vector vector=this.matrix.get(row);
        return vector.sortMin();
    }
    /**
     * 返回形状，即多少列多少行
     */
    public Matrix.Shape shape(){
        return new Matrix.Shape(row,col);
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
        public String toString() {

            return "[row][col]:["+row+","+col+"]";
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

        Double[] inX = new Double[]{0.0, 0.0};
//        double[][] vals = {{1.1, 1.2}, {2.1, 2.2}, {0., 0.}, {3.1, 3.2}};
        Double[][] vals = {{ 1., 1.1},{1. ,  1.},{0. ,  0. },{0.,  0.1}};
        Matrix dataMatrix=new Matrix(vals);

        StdOut.println("dataMatrix = " + dataMatrix);

        Matrix inXMatrix = new Matrix(inX, Matrix.Axis.row,vals.length);//两列

        StdOut.println("inXMatrix = " + inXMatrix);
        Matrix minusMatrix=inXMatrix.minus(dataMatrix);
        StdOut.println("minusMatrix = " + minusMatrix);

        Matrix posMatrix=minusMatrix.pow(2L);
        StdOut.println("posMatrix = " + posMatrix);

        Matrix plusMatrix=posMatrix.plus(Matrix.Axis.col);
        StdOut.println("plusMatrix = " + plusMatrix);

        Matrix sqrtMatrix =plusMatrix.sqrt();

        StdOut.println("sqrtMatrix = " + sqrtMatrix);

        TreeMap mapSort=sqrtMatrix.sortVectorByKey(0);


        StdOut.println("map = " + mapSort);
//        testComputer();
//        testDefaultMatrix();

    }


//
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
        Integer[][] sumData ={{ 1,2,3},{4,5,6}};
        Matrix sumMatrix=new Matrix(sumData);
        StdOut.println("sumMatrix = " + sumMatrix);

        Matrix sumResult=sumMatrix.plus(Matrix.Axis.row);
        StdOut.println("\nsumResult = " + sumResult);

        Matrix sumResultCol=sumMatrix.plus(Matrix.Axis.col);
        StdOut.println("\nsumResultCol = " + sumResultCol);

        Matrix sumResultDefaultl=sumMatrix.plus();
        StdOut.println("\nsumResultDefaultl = " + sumResultDefaultl);
    }
    public static void testDefaultMatrix() {
        Matrix a = new Matrix(2);
        Integer[] d = {1, 2};
        a.addVector(d);
        Double[][] vals = {{1., 1.1}, {1., 1.}, {0., 0.}, {0., 0.1}};
        a.addVector(vals);
        StdOut.println("a = " + a);
        Double[] val = {1., 1.1, 1., 1., 0., 0., 0., 0.1};
        a.addVector(val);
        a.addVector(1,3,4,5);
        Number[][] array= (Number[][]) a.toArray();

        Matrix aT=a.transposeC();

        Integer[] inX={3,0};
        Matrix dd=new Matrix(inX, Matrix.Axis.row,6);
        StdOut.println("dd = " + dd);

        Matrix mShape=new Matrix(vals,new Matrix.Shape(2,2));
        StdOut.println("mShape = " + mShape);
    }
}

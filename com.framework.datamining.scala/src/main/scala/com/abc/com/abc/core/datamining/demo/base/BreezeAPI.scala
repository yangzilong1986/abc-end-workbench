package com.abc.com.abc.core.datamining.demo.base

import breeze.linalg.DenseVector
import breeze.linalg._
import breeze.numerics._
import org.apache.spark.mllib.linalg.Vectors
import com.github.fommil.netlib.BLAS
import com.github.fommil.netlib.NativeRefBLAS
import com.github.fommil.netlib.F2jBLAS
object Test {
  def main(args: Array[String]) {
    val alpha = 0.1
    DenseMatrix.tabulate(3,2){case(i,j) => i+j}
    val sx = Vectors.sparse(3, Array(0, 2), Array(1.0, -2.0))
    val dx = Vectors.dense(1.0, 0.0, -2.0)
    val sy = Vectors.sparse(3, Array(0, 1), Array(2.0, 1.0))
    val dy = Vectors.dense(2.0, 1.0, 0.0)
//    axpy(alpha, dx, dy)
    val v1 = DenseVector(1.0, 2.0, 3.0, 4.0)
    val v2 = DenseVector(0.5, 0.5, 0.5, 0.5)
    // DenseVector(1.5, 2.5, 3.5, 4.5)
    println("\nv1 + v2 : ")
    println(v1 + v2)

    // DenseVector(0.5, 1.5, 2.5, 3.5)
    println("\nv1 - v2 : ")
    println(v1 - v2)

    // DenseVector(0.5, 1.0, 1.5, 2.0)
    println("\nv1 :* v2 : ")
    // 规则1：乘号前面多了冒号
    println(v1 :* v2)

    // DenseVector(2.0, 4.0, 6.0, 8.0)
    println("\nv1 :/ v2 : ")
    // 规则1：除号前面多了冒号
    println(v1 :/ v2)

    // 但是v1 和 v2并没有改变
    // DenseVector(1.0, 2.0, 3.0, 4.0)
    println("\nv1 : ")
    println(v1)

    // DenseVector(0.5, 0.5, 0.5, 0.5)
    println("\nv2 : ")
    println(v2)

    // 规则2
    // 如果想把最后的结果保存到v1上，需要加等号
    // DenseVector(1.5, 2.5, 3.5, 4.5)
    println("\nv1 += v2 : ")
    println(v1 += v2)

    // DenseVector(1.0, 2.0, 3.0, 4.0)
    println("\nv1 -= v2 : ")
    println(v1 -= v2)

    // DenseVector(0.5, 1.0, 1.5, 2.0)
    println("\nv1 :*= v2 : ")
    // 注意：乘号前面多了冒号
    println(v1 :*= v2)

    // DenseVector(1.0, 2.0, 3.0, 4.0)
    println("\nv1 :/= v2 : ")
    // 注意：除号前面多了冒号
    println(v1 :/= v2)
  }
}
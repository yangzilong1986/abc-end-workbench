package com.abc.com.abc.core.datamining.demo.streaming

import java.io.PrintWriter
import java.net.ServerSocket
import java.util.Random

/**
  * 随机线性回归的数据生成器
  */
object StreamingModelProducer {
  import breeze.linalg._

  def main(args: Array[String]): Unit = {

    //每秒处理活动的最大数
    val MaxEvents=100
    val NumFeatures=100
    val random=new Random()
    //生成服务正态分布的稠密向量的函数
    //可以使用下列函数初步生成已知的权重向量w。它在生成器的整个生命周期中固定
    def generateRandomArray(n:Int)=Array.tabulate(n){
      _=>random.nextGaussian()
    }
    //生成一个确定的随机模型权重向量
    val w=new DenseVector(generateRandomArray(NumFeatures))
    val intercept=random.nextGaussian()*10
    //生成一些随机数据事件
    def generateNoisyData(n:Int)={
      (1 to n).map{
        i=>
          val x=new DenseVector(generateRandomArray(NumFeatures))
          val y:Double=w.dot(x)
          val noisy=y+intercept
          (noisy,x)
      }
    }
    //创建网络生成器
    //创建网络套接字
    val listener=new ServerSocket(9999)
    println("监听端口 9999")
    while(true){
      val socket=listener.accept()
      new Thread(){
        override def run={
          println("连接来自:\t"+socket.getInetAddress)
          val out=new PrintWriter(socket.getOutputStream,true)

          while(true){
            Thread.sleep(1000)
            val num=random.nextInt(MaxEvents)
            val data=generateNoisyData(num)
            //
            data.foreach{
              case (y,x)=>
                val xStr=x.data.mkString(",")
                val eventStr=s"$y\t$xStr"
                out.write(eventStr)
                out.write("\n")

            }
            out.flush()
            out.close()
            println(s"Creaed $num events")
          }//out-stream-over
          socket.close()
        }
      }.start()
    }
  }

}

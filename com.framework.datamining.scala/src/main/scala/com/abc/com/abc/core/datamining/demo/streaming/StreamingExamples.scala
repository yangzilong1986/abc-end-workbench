package com.abc.com.abc.core.datamining.demo.streaming

import java.io.{File, FileInputStream, PrintWriter}
import java.net.ServerSocket

import org.apache.log4j.{Level, Logger}
import org.apache.spark.internal.Logging

import scala.util.Random

/** Utility functions for Spark Streaming examples. */
object StreamingExamples  {

  def main(args: Array[String]) {
   val random=new Random()
   //每秒最大活动数
    val MaxEvent=6
    val dataFile="D:\\DevN\\sample-data\\dadamining\\ml-100k\\u.data"
    val file = new File(dataFile);
    val nameResources = new FileInputStream(file)

    val names=scala.io.Source.fromInputStream(nameResources).getLines().toList.head.split(" ").toSeq

    names.foreach(println)

    val products=Seq(
      "iPhone Cover"->9.99,
      "Headphones"->5.49,
      "Samsung Galaxy Cover"->8.95,
      "iPad Cover"->7.49
    )

    //每秒处理活动最大数的模拟
    def generateProductEvents(n:Int)={
      (1 to n).map{
        i=>
          val (product,price)=products(random.nextInt(products.size))
          val user=random.shuffle(names).head
          (user,product,price)
      }
    }
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
            val num=random.nextInt(MaxEvent)
            val productEvents=generateProductEvents(num)
            //
            productEvents.foreach { event =>
              out.write(event.productIterator.mkString(","))
              out.write("\n")
            }
            out.flush()
            println(s"Creaed $num events")
            out.close()
          }//out-stream-over
          socket.close()
        }
      }.start()
    }
  }

  /** Set reasonable logging levels for streaming if the user has not configured log4j. */
  def setStreamingLogLevels() {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      // We first log something to initialize Spark's default logging, then we override the
      // logging level.
//      logInfo("Setting log level to [WARN] for streaming example." +
//        " To override add a custom log4j.properties to the classpath.")
      Logger.getRootLogger.setLevel(Level.WARN)
    }
  }
}

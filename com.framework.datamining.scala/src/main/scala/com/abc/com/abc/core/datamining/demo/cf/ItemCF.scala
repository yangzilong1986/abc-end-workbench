package com.abc.com.abc.core.datamining.demo.cf

import com.abc.com.abc.core.datamining.cf.{ItemRef, ItemSimilarity, RecommendedItem}
import com.abc.com.abc.core.datamining.demo.AbstractParams
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import scopt.OptionParser

import scala.collection.mutable


object ItemCF {

  case class Params(
                     input: String = null,
                     kryo: Boolean = false,
                     numIterations: Int = 20,
                     lambda: Double = 1.0,
                     rank: Int = 10,
                     numUserBlocks: Int = -1,
                     numProductBlocks: Int = -1,
                     implicitPrefs: Boolean = false) extends AbstractParams[Params]

  def main(args: Array[String]) {
    val defaultParams = Params()

    val parser = new OptionParser[Params]("ItemCF") {
      head("MovieLensALS: an example app for ALS on MovieLens data.")
      opt[Int]("rank")
        .text(s"rank, default: ${defaultParams.rank}")
        .action((x, c) => c.copy(rank = x))
      opt[Int]("numIterations")
        .text(s"number of iterations, default: ${defaultParams.numIterations}")
        .action((x, c) => c.copy(numIterations = x))
      opt[Double]("lambda")
        .text(s"lambda (smoothing constant), default: ${defaultParams.lambda}")
        .action((x, c) => c.copy(lambda = x))
      opt[Unit]("kryo")
        .text("use Kryo serialization")
        .action((_, c) => c.copy(kryo = true))
      opt[Int]("numUserBlocks")
        .text(s"number of user blocks, default: ${defaultParams.numUserBlocks} (auto)")
        .action((x, c) => c.copy(numUserBlocks = x))
      opt[Int]("numProductBlocks")
        .text(s"number of product blocks, default: ${defaultParams.numProductBlocks} (auto)")
        .action((x, c) => c.copy(numProductBlocks = x))
      opt[Unit]("implicitPrefs")
        .text("use implicit preference")
        .action((_, c) => c.copy(implicitPrefs = true))
      arg[String]("<input>")
        .required()
        .text("input paths to a MovieLens dataset of ratings")
        .action((x, c) => c.copy(input = x))
      note(
        """
          |For example, the following command runs this app on a synthetic dataset:
          |
          | bin/spark-submit --class org.apache.spark.examples.mllib.MovieLensALS \
          |  examples/target/scala-*/spark-examples-*.jar \
          |  --rank 5 --numIterations 20 --lambda 1.0 --kryo \
          |  data/mllib/sample_movielens_data.txt
        """.stripMargin)
    }

    run(defaultParams)

    //    parser.parse(args, defaultParams) match {
    //      case Some(params) => run(params)
    //      case _ => sys.exit(1)
    //    }
  }

  def run(params: Params): Unit = {
    val conf = new SparkConf().setAppName(s"MovieLensALS with $params")
    val sc = new SparkContext(conf)

//    val a = sc.parallelize(Array(("123",4.0),("456",9.0),("789",9.0)))
//    val b = sc.parallelize(Array(("123",8.0),("789",10)))

    val a = sc.parallelize(Array(("123",4.0),("456",9.0),("789",5.0)))
    val b = sc.parallelize(Array(("123",8.0),("789",10)))

    val c = a.join(b)
    //(123,(4.0,8.0))
    //(789,(9.0,10))

    val d = a.cogroup(b)
    //    (456,(CompactBuffer(9.0),CompactBuffer()))
    //    (123,(CompactBuffer(4.0),CompactBuffer(8.0)))
    //    (789,(CompactBuffer(9.0),CompactBuffer(10)))

    val e = a.leftOuterJoin(b)
    //    (456,(9.0,None))
    //    (123,(4.0,Some(8.0)))
    //    (789,(9.0,Some(10)))

    val f = a.fullOuterJoin(b)
    //    (456,(Some(9.0),None))
    //    (123,(Some(4.0),Some(8.0)))
    //    (789,(Some(9.0),Some(10)))

    val g = a.cartesian(b)
    //    ((123,4.0),(123,8.0))
    //    ((123,4.0),(789,10))
    //    ((456,9.0),(123,8.0))
    //    ((456,9.0),(789,10))
    //    ((789,9.0),(123,8.0))
    //    ((789,9.0),(789,10))

    //    val a = sc.parallelize(Array(("123",4.0),("456",9.0),("789",5.0)))
    //    val b = sc.parallelize(Array(("123",8.0),("789",10)))
    //    ((123,4.0),(123,8.0))
    //    ((123,4.0),(789,10))
    //    ((456,9.0),(123,8.0))
    //    ((456,9.0),(789,10))
    //    ((789,5.0),(123,8.0))
    //    ((789,5.0),(789,10))

    val i = a.keyBy{case (k,v)=>("abc",234)}
    //    ((abc,234),(123,4.0))
    //    ((abc,234),(456,9.0))
    //    ((abc,234),(789,9.0))

    Logger.getRootLogger.setLevel(Level.WARN)

    val implicitPrefs = params.implicitPrefs

    //训练数据文件路径
    val trainDataFile = "D:\\DevN\\sample-data\\spark-data\\cf\\cfitem.txt"

    val argFile = if (params.input == "") params.input else trainDataFile
    val rawData = sc.textFile(argFile)
    val rawItemsData = rawData.map(_.split(",").take(3)) //.first()

    //节点存储
    val userData=rawItemsData.map(f=>(ItemRef(f(0),f(1),f(2).toDouble))).cache()

    //建立模型
    val simil=new ItemSimilarity()
    //训练数据
    val simil_rdd= simil.Similarity(userData,"cooccurrence")
    //打印结果
    println(s"物品相似度矩阵:${simil_rdd.count()}")
//    simil_rdd.foreach(println)
    val recommd=new RecommendedItem
//    val recommd_rdd1=recommd.Recommand(simil_rdd,userData,30)
    val recommd_rdd1=recommd.Recommand(simil_rdd,userData)
    recommd_rdd1.count()

    println("用户推荐")
    recommd_rdd1.foreach(println)
    //
    sc.stop()

  }

}

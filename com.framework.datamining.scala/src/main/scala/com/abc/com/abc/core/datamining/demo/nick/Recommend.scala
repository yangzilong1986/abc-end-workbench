package com.abc.com.abc.core.datamining.demo.nick

import com.abc.com.abc.core.datamining.demo.AbstractParams

import scala.collection.mutable
import org.apache.log4j.{Level, Logger}
import scopt.OptionParser
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.jblas.DoubleMatrix

/**
  * An example app for ALS on MovieLens data (http://grouplens.org/datasets/movielens/).
  * Run with
  * {{{
  * bin/run-example org.apache.spark.examples.mllib.MovieLensALS
  * }}}
  * A synthetic dataset in MovieLens format can be found at `data/mllib/sample_movielens_data.txt`.
  * If you use it as a template to create your own app, please use `spark-submit` to submit your app.
  */
object Recommend {

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

    val parser = new OptionParser[Params]("MovieLensALS") {
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
    if (params.kryo) {
      conf.registerKryoClasses(Array(classOf[mutable.BitSet], classOf[Rating]))
        .set("spark.kryoserializer.buffer", "8m")
    }
    val sc = new SparkContext(conf)

    //向用户推荐问题
    recommendProducts(sc,params)
    //物品推荐，给定一个物品推荐哪些物品和它相似
    //recommendItem(sc,params)
  }

  def recommendItem(sc :SparkContext,params: Params): Unit = {
    Logger.getRootLogger.setLevel(Level.WARN)

    val implicitPrefs = params.implicitPrefs

    //训练数据文件路径
    val trainDataFile= "D:\\DevN\\sample-data\\dadamining\\ml-100k\\u.data"

    val argFile=if(params.input=="") params.input else trainDataFile
    val rawData = sc.textFile(argFile)

    val rawRatings=rawData.map(_.split("\t").take(3))//.first()
    val ratings=rawRatings.map{
        case Array(user,movie,rating)=>Rating(user.toInt,movie.toInt,rating.toDouble)
      }

    //训练结果
    val model=ALS.train(ratings,50,10,0.01)

    val itemId=567
    //计算相似度
    val itemFactor=model.productFeatures.lookup(itemId).head
     val itemVector=new DoubleMatrix(itemFactor)
    val s=cosineSimilarity(itemVector,itemVector)
    println("余弦相似度")
    println(s)
    //计算各物品的余弦相似度
    val sims=model.productFeatures.map{
      case (id,factor)=>
        val factorVector=new DoubleMatrix(factor)
        val sim=cosineSimilarity(factorVector,itemVector)
      (id,sim)
    }
    //对物品按照相似度排序然后取出与物品567最相似的前10个物品
    val sortedSims=sims.top(10)(Ordering.by[(Int,Double),Double]{
      case(id,similarity)=>similarity
    })
    println(sortedSims.take(10).mkString("\n"))

    //检查推荐内容
    val itemDataFile= "D:\\DevN\\sample-data\\dadamining\\ml-100k\\u.item"
    val movies = sc.textFile(itemDataFile)
    val titles=movies.map(line=>line.split("\\|").take(2)).map(array=>(array(0).toInt,array(1))).collectAsMap()

    val sortedSims2=sims.top(10+1)(Ordering.by[(Int,Double),Double]{
      case(id,similarity)=>similarity
    })

    val items=sortedSims2.slice(1,11).map{
      case (id,sim)=>(titles(id),sim)
    }.mkString("\n")
    println(items)
    sc.stop()
  }

  /**
    * 余弦相似度计算
    * @param vec1
    * @param vec2
    */
  def cosineSimilarity(vec1:DoubleMatrix,vec2:DoubleMatrix): Double ={
    vec1.dot(vec2)/(vec1.norm2()*vec2.norm2())
  }

  def recommendProducts(sc :SparkContext,params: Params): Unit = {
    Logger.getRootLogger.setLevel(Level.WARN)

    val implicitPrefs = params.implicitPrefs

    //训练数据文件路径
    val trainDataFile= "D:\\DevN\\sample-data\\dadamining\\ml-100k\\u.data"

    val argFile=if(params.input=="") params.input else trainDataFile
    val rawData = sc.textFile(argFile)

    //val sigleData=rawData.first()
    //println(sigleData)

    val rawRatings=rawData.map(_.split("\t").take(3))//.first()

    val ratings=rawRatings.map{
      case Array(user,movie,rating)=>Rating(user.toInt,movie.toInt,rating.toDouble)
    }
    val sigleData=ratings.first()
    println(sigleData)

    val model=ALS.train(ratings,50,10,0.01)

    //model.productFeatures.collect()
    model.userFeatures.count()

    val userId=789
    val predictedRating=model.predict(userId,123)
    println(predictedRating)


    val K=10

    val topKRecs=model.recommendProducts(userId,10)
    println(topKRecs.mkString("\n"))


    //检查推荐内容
    val itemDataFile= "D:\\DevN\\sample-data\\dadamining\\ml-100k\\u.item"
    val movies = sc.textFile(itemDataFile)
    val titles=movies.map(line=>line.split("\\|").take(2)).map(array=>(array(0).toInt,array(1))).collectAsMap()

    println("检查内容")
    println(titles(123))

    //针对用户，找出他接触过的电影
    val moviesForUser=ratings.keyBy(_.user).lookup(789)
    println("针对用户，找出他接触过的电影")
    println(moviesForUser.size)
    //打印输出
    //moviesForUser.foreach(println)
    //获取评价级高的十部电影
    moviesForUser.sortBy(-_.rating).take(10).map(rating=>(titles(rating.product),rating.rating)).foreach(println)
    println("针对用户，推荐的影片")
    topKRecs.map(rating=>(titles(rating.product),rating.rating)).foreach(println)

    //推荐模型效果的评估
    //均方差Mean Squared Error MSE,显示评分方法
    //MSE定义为各平方误差的和与总数目的商。其中，平方误差是指预测到的评级与真实评级的差值的平方
    val actualRading=moviesForUser.take(1)(0)//Rating(789,1012,4.0)
    val predictedRatingProduct=model.predict(789,actualRading.product)
    //实际评分与预计评级的平方误差
    val squaredError=math.pow(predictedRatingProduct-actualRading.rating,2.0)



    sc.stop()
  }

}

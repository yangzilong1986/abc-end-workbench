package com.abc.com.abc.core.datamining.demo.nick

import com.abc.com.abc.core.datamining.demo.AbstractParams

import scala.collection.mutable
import org.apache.log4j.{Level, Logger}
import scopt.OptionParser
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

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

    model.userFeatures.count()

    val predictedRating=model.predict(789,123)
    println(predictedRating)

    val userId=789
    val K=10

    val topKRecs=model.recommendProducts(userId,10)
    println(topKRecs.mkString("\n"))
    sc.stop()

  }

}

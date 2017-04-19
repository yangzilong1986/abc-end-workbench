package com.abc.framework.samples.mia.recommender.ch04;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
//import org.apache.mahout.cf.taste.example.grouplens.GroupLensDataModel;
//import org.apache.mahout.cf.taste.example.grouplens.GroupLensDataModel;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
//import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import java.io.File;

final class GroupLens10MEvalIntro {
  public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";
  private GroupLens10MEvalIntro() {
  }

  public static void main(String[] args) throws Exception {
    File file=new File(OUT_DIR+"ml-latest-small/ratings.csv");
//    DataModel model = new GroupLensDataModel(file);
    DataModel model =new FileDataModel(file);
    RecommenderEvaluator evaluator =
      new AverageAbsoluteDifferenceRecommenderEvaluator();
    RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
      @Override
      public Recommender buildRecommender(DataModel model) throws TasteException {
        //相关系数
        UserSimilarity similarity =
        //皮尔逊相关系数
        //new PearsonCorrelationSimilarity(model);
        //欧式距离定义相似度
        //new EuclideanDistanceSimilarity(model);
        //基于对数概率的相似度
        new LogLikelihoodSimilarity( model);
        UserNeighborhood neighborhood =//100说明推荐所依赖的最相似的用户为100个
//          new NearestNUserNeighborhood(100, similarity, model);
          new ThresholdUserNeighborhood(0.75, similarity, model);

        //基于用户的推荐
        return new GenericUserBasedRecommender(model, neighborhood, similarity);
      }
    };
    //评估推荐程序的精度
    //.95为使用95%的数据来构建要评估的模型，剩余的5%来做测试数据
    double score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05);
    System.out.println(score);

  }

}

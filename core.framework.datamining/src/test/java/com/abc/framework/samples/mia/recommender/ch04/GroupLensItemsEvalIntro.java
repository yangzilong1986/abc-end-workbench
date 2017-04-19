package com.abc.framework.samples.mia.recommender.ch04;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;


final class GroupLensItemsEvalIntro {
    public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";
    public static void main(String[] args) throws Exception {
        File file=new File(OUT_DIR+"ml-latest-small/ratings.csv");
//    DataModel model = new GroupLensDataModel(file);
        DataModel model =new FileDataModel(file);
        RecommenderEvaluator evaluator =
                new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder recommenderBuilder
           = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel model) throws TasteException {
                //皮尔逊相关系数
                ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
                //基于物品的推荐
                return new GenericItemBasedRecommender(model, similarity);
                //基于用户的推荐
//                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };
        //评估推荐程序的精度
        //.95为使用95%的数据来构建要评估的模型，剩余的5%来做测试数据
//        recommenderBuilder=new RecommenderBuilder() {
//            @Override
//            public Recommender buildRecommender(DataModel model) throws TasteException {
//                return SlopeOneNoWeighting.buildRecommender(model);
//            }
//        };

        recommenderBuilder=new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel model) throws TasteException {
                return KnnBasedRecommender.buildRecommender(model);
            }
        };

        double score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05);
        System.out.println(score);
    }

}
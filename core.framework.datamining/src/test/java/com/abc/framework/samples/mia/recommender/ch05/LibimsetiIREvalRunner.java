package com.abc.framework.samples.mia.recommender.ch05;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;

class LibimsetiIREvalRunner {
  public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";
  private LibimsetiIREvalRunner() {
  }

  public static void main(String[] args) throws Exception {
    File file=new File(OUT_DIR+"libimseti/ratings.dat");
//    DataModel model = new GroupLensDataModel(file);
    DataModel model =new FileDataModel(file);
    model = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(model));
      RecommenderIRStatsEvaluator evaluator =
        new GenericRecommenderIRStatsEvaluator();
      RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
        @Override
        public Recommender buildRecommender(DataModel model) throws TasteException {
          UserSimilarity similarity = new TanimotoCoefficientSimilarity(model);
          UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
          return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
        }
      };
      IRStatistics stats = evaluator.evaluate(recommenderBuilder, null, model, null, 10, Double.NaN, 0.1);
      System.out.println(stats);
  }

}
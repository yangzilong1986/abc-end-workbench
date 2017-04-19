package com.abc.framework.samples.mia.recommender.ch05;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.io.File;
import java.io.IOException;

class LibimsetiEvalRunner {
  public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";
  private LibimsetiEvalRunner() {
  }

  public static void main(String[] args) throws Exception {
//    DataModel model = new FileDataModel(new File("ratings.dat"));
    File file=new File(OUT_DIR+"/libimseti/ratings.dat");
//    DataModel model = new GroupLensDataModel(file);
    DataModel model =new FileDataModel(file);
    RecommenderEvaluator evaluator =
      new AverageAbsoluteDifferenceRecommenderEvaluator();

    RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
      @Override
      public Recommender buildRecommender(DataModel model) throws TasteException {
        try {
          return new LibimsetiRecommender(model);
        } catch (IOException ioe) {
          throw new TasteException(ioe);
        }

      }
    };
    double score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.1);
    System.out.println(score);
  }

}

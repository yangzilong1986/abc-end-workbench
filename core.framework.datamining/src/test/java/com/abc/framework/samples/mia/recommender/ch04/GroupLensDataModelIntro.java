package com.abc.framework.samples.mia.recommender.ch04;

//import org.apache.mahout.cf.taste.example.grouplens.GroupLensDataModel;

import org.apache.mahout.cf.taste.example.grouplens.GroupLensDataModel;
import org.apache.mahout.cf.taste.impl.eval.LoadEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
//import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import java.io.File;

class GroupLensDataModelIntro {
    public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";

    private GroupLensDataModelIntro() {
    }

    public static void main(String[] args) throws Exception {
//    DataModel model = new GroupLensDataModel(new File("ratings.dat"));
        File file = new File(OUT_DIR + "/ml-latest-small/ratings.csv");
//    DataModel model = new GroupLensDataModel(file);
        DataModel model = new FileDataModel(file);
        //推荐用户相似的100用户
        //用户间相似性
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //用户邻域
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(100, similarity, model);
        Recommender recommender =//基于用户的推荐系统
                new GenericUserBasedRecommender(model, neighborhood, similarity);
        LoadEvaluator.runLoad(recommender);
    }

}

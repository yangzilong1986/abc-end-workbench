package com.abc.framework.samples.mia.recommender.ch02;

import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

import java.io.*;
import java.util.*;

class RecommenderIntro {
    public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";

    private RecommenderIntro() {
    }

    public static void main(String[] args) throws Exception {

        DataModel model = new FileDataModel(new File(OUT_DIR + "intro.csv"));

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //相似用户
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(2, similarity, model);
        //推荐引擎
        Recommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);
        //为用户1推荐一个产品
        List<RecommendedItem> recommendations =
                recommender.recommend(1, 1);

        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }

    }

}


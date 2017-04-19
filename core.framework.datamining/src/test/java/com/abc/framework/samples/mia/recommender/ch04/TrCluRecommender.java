package com.abc.framework.samples.mia.recommender.ch04;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.ClusterSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.FarthestNeighborClusterSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.TreeClusteringRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.ConjugateGradientOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.NonNegativeQuadraticOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.Optimizer;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by admin on 2017/4/17.
 */
public class TrCluRecommender {
    static Recommender buildRecommender(DataModel model) {
        UserSimilarity similarity = new LogLikelihoodSimilarity(model);
        ClusterSimilarity clusterSimilarity = new FarthestNeighborClusterSimilarity(similarity);
        try {
            return new TreeClusteringRecommender(model, clusterSimilarity, 10);
        }catch ( TasteException e ){
            e.printStackTrace();;
            return null;
        }
    }
}

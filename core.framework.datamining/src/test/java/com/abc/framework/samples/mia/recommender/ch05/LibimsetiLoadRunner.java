package com.abc.framework.samples.mia.recommender.ch05;

import org.apache.mahout.cf.taste.impl.eval.LoadEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.io.File;

class LibimsetiLoadRunner {
    public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";

    private LibimsetiLoadRunner() {
    }

    public static void main(String[] args) throws Exception {
        File file = new File(OUT_DIR + "libimseti/ratings.dat");
//    DataModel model = new GroupLensDataModel(file);
        DataModel model = new FileDataModel(file);
        Recommender rec = new LibimsetiRecommender(model);
        LoadEvaluator.runLoad(rec);
    }

}
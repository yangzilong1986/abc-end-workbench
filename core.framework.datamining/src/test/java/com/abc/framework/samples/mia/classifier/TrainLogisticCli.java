package com.abc.framework.samples.mia.classifier;

import org.apache.mahout.classifier.sgd.RunLogistic;
import org.apache.mahout.classifier.sgd.TrainLogistic;

import java.io.IOException;

/**
 * Created by admin on 2017/4/19.
 */
public class TrainLogisticCli {
    public static final String OUT_DIR="core.framework.datamining/target/test-classes/classifydonut/";
    public static void main(String[] args) throws IOException {
//        trainLogistic();
        runLogistic();
    }

    public static void runLogistic() throws IOException {
        String[] arg={"--input",OUT_DIR+"donut.csv",
                "--model",OUT_DIR+"model",
                "--auc",
                "--confusion"
        };
        RunLogistic.main(arg);
    }

    public static void trainLogistic() throws IOException {
        String[] arg={"--input",OUT_DIR+"donut.csv",
                "--output",OUT_DIR+"model",
                "--target","color",
                "--categories","2",
                "--predictors","x","y",
                "--types","numeric",
                "--features","20",
                "--passes","100",
                "--rate","50"
        };
        TrainLogistic.main(arg);
    }
}

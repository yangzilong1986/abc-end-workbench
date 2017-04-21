package com.abc.framework.samples.mia.classifier.ch14;

import org.apache.mahout.classifier.bayes.PrepareTwentyNewsgroups;
import org.apache.mahout.classifier.bayes.TestClassifier;
import org.apache.mahout.classifier.bayes.TrainClassifier;

import java.io.IOException;

/**
 * Created by admin on 2017/4/19.
 * 朴素贝叶斯
 */
public class PrepareTwentyNewsgroupsCli {
    public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/20news-bydate/";

    public static void main(String[] args) throws Exception {
//        prepareTwentyNewsgroups();
//        prepareTwentyNewsgroupsTest();
        trainClassifier();
//        testClassifier();
    }

    public static void testClassifier() throws Exception {
        String[] arg = {"-d", OUT_DIR+"20news-train-test",
                "-m", OUT_DIR + "20news-model",
//                "-type","cbayes",
                "-ng","1",
                "-source","hdfs",
                "-method","sequential"
        };
        TestClassifier.main(arg);
    }

    public static void trainClassifier() throws Exception {
        String[] arg = {"-i", OUT_DIR+"20news-train",
                "-o", OUT_DIR + "20news-model",
//                "-type","cbayes",
                "-ng","1",
                "-source","hdfs"
        };
        TrainClassifier.main(arg);
    }


    public static void prepareTwentyNewsgroupsTest() throws Exception {
        String[] arg = {"-p", OUT_DIR+"20news-bydate-test",
                "-o", OUT_DIR + "20news-train-test",
                "-a","org.apache.lucene.analysis.standard.StandardAnalyzer",
                "-c","UTF-8"
        };
        PrepareTwentyNewsgroups.main(arg);
    }

    /**
     * 提取训练数据
     * @throws Exception
     */
    public static void prepareTwentyNewsgroups() throws Exception {
        String[] arg = {"-p", OUT_DIR+"20news-bydate-train",
                "-o", OUT_DIR + "20news-train",
                "-a","org.apache.lucene.analysis.standard.StandardAnalyzer",
                "-c","UTF-8"
        };
        PrepareTwentyNewsgroups.main(arg);
    }

}

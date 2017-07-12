package com.abc.framework.samples.mia.clustering.ch09;

import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.utils.clustering.ClusterDumper;

/**
 * Created by admin on 2017/4/18.
 */
public class KMeansClusteringCli {
    public static final String OUT_DIR = "core.framework.datamining/target/test-classes/reuters/";

    public static void main(String args[]) throws Exception {
        clusterDump();
    }

    public static void clusterDump() throws Exception {
        String vectorsDir = OUT_DIR + "reuters-vectors/tfidf-vectors";
        String[] arg = {"-dt", "sequencefile",
                "-d", OUT_DIR + "reuters-vectors/dictionary.file-*",
                "-s", OUT_DIR + "reuters-kmeans-clusters/clusters-10",
                "-b", "10",
                "-n", "10"
        };
        ClusterDumper.main(arg);
    }

    public static void kmeansDriver() throws Exception {
        String vectorsDir = OUT_DIR + "reuters-vectors/tfidf-vectors";
        String[] arg = {"-i", vectorsDir,
                "-c", OUT_DIR + "reuters-initial-clusters",
                "-o", OUT_DIR + "reuters-kmeans-clusters",
                "-dm", "org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure",
                "-cd", "1.0",
                "-k", "20",
                "-x", "20",
                "-cl"
        };
        KMeansDriver.main(arg);
    }
}

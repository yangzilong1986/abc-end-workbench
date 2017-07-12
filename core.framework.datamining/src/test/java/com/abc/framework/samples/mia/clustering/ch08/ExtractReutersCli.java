package com.abc.framework.samples.mia.clustering.ch08;

import org.apache.lucene.benchmark.utils.ExtractReuters;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;

/**
 * Created by admin on 2017/4/18.
 */
public class ExtractReutersCli {
    //    public static final String OUT_DIR="core.framework.datamining/target/test-classes/";
    public static final String OUT_DIR = "D:/DevN/sample-data/dadamining/";

    public static void main(String args[]) throws Exception {
//        extractReuters();
//        sequenceFilesFromDirectory();
        sparseVectorsFromSequenceFiles();
    }

    public static void sparseVectorsFromSequenceFiles() throws Exception {
        String[] arg = {"-i", OUT_DIR + "reuters/reuters-seqfiles",
                "-o", OUT_DIR + "reuters/reuters-vectors",
                "-ow"
        };
        SparseVectorsFromSequenceFiles.main(arg);
    }

    public static void sequenceFilesFromDirectory() throws Exception {
        String[] arg = {"-c", "UTF-8",
                "-i", OUT_DIR + "reuters/reuters-extracted/",
                "-o",
                OUT_DIR + "reuters/reuters-seqfiles"};
        SequenceFilesFromDirectory.main(arg);
    }


    public static void extractReuters() throws Exception {
        String[] arg = {OUT_DIR + "resources/reuters21578",
                OUT_DIR + "reuters/reuters-extracted"};
        ExtractReuters.main(arg);
    }
}

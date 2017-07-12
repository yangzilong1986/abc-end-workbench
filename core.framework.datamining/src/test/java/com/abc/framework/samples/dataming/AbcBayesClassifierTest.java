package com.abc.framework.samples.dataming;

import com.abc.basic.datamining.classify.AbcBayesClassifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/5/18.
 */
public class AbcBayesClassifierTest {

    @Test
    public void trainBayesClassifier(){
        AbcBayesClassifier bayesClassifier=new AbcBayesClassifier();
        bayesClassifier.train();
    }
    @Test
    public void classifyBayesClassifier(){
        AbcBayesClassifier bayesClassifier=new AbcBayesClassifier();
        List<String> testTrain=new ArrayList<>();
        testTrain.add("love");
        testTrain.add("my");
        testTrain.add("dalmation");
        bayesClassifier.setTestTrain(testTrain);
        bayesClassifier.classify();
    }
}

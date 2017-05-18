package com.abc.framework.samples.dataming;

import com.abc.basic.datamining.classify.AbcLogisticRegresClassifer;
import org.junit.Test;

/**
 * Created by admin on 2017/5/18.
 */
public class AbcLogisticRegresClassiferTest {

    @Test
    public void tainLogisticRegres(){
        AbcLogisticRegresClassifer<Integer,Double> logisticRegresClassifer=new AbcLogisticRegresClassifer();
        logisticRegresClassifer.train();

    }
    @Test
    public void classifyLogisticRegres(){
        AbcLogisticRegresClassifer logisticRegresClassifer=new AbcLogisticRegresClassifer();
        logisticRegresClassifer.classify();

    }
}

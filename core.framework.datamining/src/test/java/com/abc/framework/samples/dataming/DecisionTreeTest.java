package com.abc.framework.samples.dataming;

import com.abc.basic.datamining.classify.AbcTreeClassifier;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DecisionTreeTest {

    @Test
    public void testClassify(){
        AbcTreeClassifier<String, String> dTree = new AbcTreeClassifier();
        // ['pre','myope','no','reduced']
        //['young','myope','yes','normal'])
        List list=new LinkedList();
        list.add("young");
        list.add("myope");
        list.add("yes");
        list.add("normal");
        dTree.setTestData(list);
        Map map=dTree.classify();
        System.out.println("result:"+map);
    }
    @Test
    public void testTrain(){
        AbcTreeClassifier<String, String> dTree = new AbcTreeClassifier();
        dTree.train();
    }
}

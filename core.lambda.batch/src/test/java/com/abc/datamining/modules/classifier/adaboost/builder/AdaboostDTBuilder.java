package com.abc.datamining.modules.classifier.adaboost.builder;

import java.net.URISyntaxException;

import com.abc.datamining.modules.classifier.decisiontree.DecisionTree;
import com.abc.datamining.modules.classifier.decisiontree.builder.Builder;
import com.abc.datamining.utils.ShowUtils;
import com.abc.datamining.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;

public class AdaboostDTBuilder {
	
	public static String TRAIN_PATH = null;
	
	public static String TEST_PATH = null;
	
	static {
		try {
			TRAIN_PATH = AdaboostDTBuilder.class.getClassLoader().getResource("trainset/decisiontree.txt").toURI().getPath();
			TEST_PATH = AdaboostDTBuilder.class.getClassLoader().getResource("testset/decisiontree.txt").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Builder treeBuilder = new DecisionTreeC45Builder();
		DecisionTree decisionTree = new DecisionTree(TRAIN_PATH, TEST_PATH, treeBuilder);
		Object[] results = decisionTree.run();
		ShowUtils.printToConsole(results);
	}
	
}

package com.abc.datamining.modules.classifier.decisiontree;

import com.abc.datamining.modules.classifier.decisiontree.builder.Builder;
import com.abc.datamining.modules.classifier.decisiontree.data.Data;
import com.abc.datamining.modules.classifier.decisiontree.node.TreeNode;
import com.abc.datamining.modules.classifier.decisiontree.data.DataLoader;

public class DecisionTreeUtils {

	public static TreeNode build(Data data, Builder builder) {
		return (TreeNode) builder.build(data);
	}
	
	public static TreeNode build(String path, Builder builder) {
		Data data = DataLoader.loadWithId(path);
		return (TreeNode) builder.build(data);
	}
	
	public static Object[] classify(TreeNode treeNode, Data data) {
		return (Object[]) treeNode.classify(data);
	}
	
	public static Object[] classify(TreeNode treeNode, String path) {
		Data data = DataLoader.loadNoId(path);
		return (Object[]) treeNode.classify(data);
	}
	
}

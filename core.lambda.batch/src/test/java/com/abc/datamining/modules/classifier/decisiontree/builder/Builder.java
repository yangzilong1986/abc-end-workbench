package com.abc.datamining.modules.classifier.decisiontree.builder;

import com.abc.datamining.modules.classifier.decisiontree.data.Data;

public interface Builder {

	/**
	 * 构造决策树
	 * @param data
	 * @return
	 */
	public Object build(Data data);
}

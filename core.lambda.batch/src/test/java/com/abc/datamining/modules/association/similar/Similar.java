package com.abc.datamining.modules.association.similar;

import com.abc.datamining.common.document.DocumentLoader;
import com.abc.datamining.common.distance.CosineDistance;
import com.abc.datamining.common.document.DocumentSet;
import com.abc.datamining.common.document.DocumentUtils;

public class Similar {

	public static void main(String[] args) throws Exception {
		String path = Similar.class.getClassLoader().getResource("测试").toURI().getPath();
		DocumentSet dataSet = DocumentLoader.loadDocumentSet(path);
		DocumentUtils.calculateTFIDF_0(dataSet.getDocuments());
		DocumentUtils.calculateSimilarity(dataSet.getDocuments(), new CosineDistance());
	}
}

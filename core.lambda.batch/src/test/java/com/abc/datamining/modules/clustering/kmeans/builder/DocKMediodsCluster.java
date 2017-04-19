package com.abc.datamining.modules.clustering.kmeans.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import com.abc.datamining.common.document.Document;
import com.abc.datamining.common.document.DocumentLoader;
import com.abc.datamining.common.document.DocumentSet;
import com.abc.datamining.common.document.DocumentUtils;
import com.abc.datamining.modules.clustering.kmeans.data.DataPoint;
import com.abc.datamining.modules.clustering.kmeans.data.DataPointCluster;
import com.abc.datamining.utils.DistanceUtils;

public class DocKMediodsCluster extends AbstractCluster {
	
	//阀值
	public static final double THRESHOLD = 0.028;
	//迭代次数
	public static final int ITER_NUM = 10;
	
	/*
	 * 初始化数据
	 */
	public List<DataPoint> initData() {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSetByThread(path);
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			for(Document document : documents) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(document.getTfidfWords());
				dataPoint.setCategory(document.getCategory());
				dataPoints.add(dataPoint);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataPoints;
	}
	
	//随机生成中心点，并生成初始的K个聚类
	public List<DataPointCluster> genInitCluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		Random random = new Random();
		Set<String> categories = new HashSet<String>();
		while (clusters.size() < k) {
			DataPoint center = points.get(random.nextInt(points.size()));
			String category = center.getCategory();
			if (categories.contains(category)) continue;
			categories.add(category);
			DataPointCluster cluster = new DataPointCluster();
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	//将点归入到聚类中
	public void handleCluster(List<DataPoint> points, List<DataPointCluster> clusters, int iterNum) {
		System.out.println("iterNum: " + iterNum);
		for (DataPoint point : points) {
			DataPointCluster maxCluster = null;
			double maxDistance = Integer.MIN_VALUE;
			for (DataPointCluster cluster : clusters) {
				DataPoint center = cluster.getCenter();
				double distance = DistanceUtils.cosine(point.getValues(), center.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					maxCluster = cluster;
				}
			}
			if (null != maxCluster) {
				maxCluster.getDataPoints().add(point);
			}
		}
		//终止条件定义为原中心点与新中心点距离小于一定阀值
		//当然也可以定义为原中心点等于新中心点
		boolean flag = true;
		for (DataPointCluster cluster : clusters) {
			DataPoint center = cluster.getCenter();
			DataPoint newCenter = cluster.computeMediodsCenter();
			double distance = DistanceUtils.cosine(
					newCenter.getValues(), center.getValues());
			System.out.println("distaince: " + distance);
			if (distance > THRESHOLD) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		System.out.println("--------------");
		if (!flag && iterNum < ITER_NUM) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters, ++iterNum);
		}
	}
	
	public List<Map.Entry<String, Double>> sortMap(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = 
				new ArrayList<Map.Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				if (o1.getValue().isNaN()) {
					o1.setValue(0.0);
				}
				if (o2.getValue().isNaN()) {
					o2.setValue(0.0);
				}
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		return list;
	}
	
	public void build() {
		List<DataPoint> points = initData();
		List<DataPointCluster> clusters = genInitCluster(points, 4);
		for (DataPointCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter().getCategory());
		}
		handleCluster(points, clusters, 0);
		int success = 0, failure = 0;
		for (DataPointCluster cluster : clusters) {
			String category = cluster.getCenter().getCategory();
			System.out.println("center: " + category + "--" + cluster.getDataPoints().size());
			for (DataPoint dataPoint : cluster.getDataPoints()) {
				String dpCategory = dataPoint.getCategory();
				System.out.println(dpCategory);
				if (category.equals(dpCategory)) {
					success++;
				} else {
					failure++;
				}
			}
			System.out.println("----------");
		}
		System.out.println("total: " + (success + failure) + " success: " + success + " failure: " + failure);
	}
	
	public static void main(String[] args) {
		new DocKMediodsCluster().build();
	}
	
}

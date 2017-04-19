package com.abc.datamining.modules.clustering.dbscan.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.abc.datamining.common.document.Document;
import com.abc.datamining.common.document.DocumentLoader;
import com.abc.datamining.common.document.DocumentSet;
import com.abc.datamining.common.document.DocumentUtils;
import com.abc.datamining.modules.clustering.dbscan.data.DataPoint;
import com.abc.datamining.modules.clustering.kmeans.builder.DocKMediodsCluster;
import com.abc.datamining.utils.DistanceUtils;

public class DocDBScanBuilder {
	
	//半径
	public static double EPISLON = 0.04;
	//密度、最小点个数
	public static int MIN_POINTS = 15;
	
	//初始化数据
	public List<DataPoint> initData() {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSetByThread(path);
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			for(Document doc : documents) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(doc.getTfidfWords());
				dataPoint.setCategory(doc.getCategory());
				dataPoints.add(dataPoint);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataPoints;
	}
	
	//获取当前点的邻居
	public List<DataPoint> obtainNeighbors(DataPoint current, List<DataPoint> points) {
		List<DataPoint> neighbors = new ArrayList<DataPoint>();
		for (DataPoint point : points) {
			double distance = DistanceUtils.cosine(current.getValues(), point.getValues());
//			System.out.println("distance: " + distance);
			if (distance > EPISLON) {
				neighbors.add(point);
			}
		}
		return neighbors;
	}
	
	public void mergeCluster(DataPoint point, List<DataPoint> neighbors,
			int clusterId, List<DataPoint> points) {
		point.setClusterId(clusterId);
		for (DataPoint neighbor : neighbors) {
			//邻域点中未被访问的点先观察是否是核心对象
			//如果是核心对象，则其邻域范围内未被聚类的点归入当前聚类中
			if (!neighbor.isAccessed()) {
				neighbor.setAccessed(true);
				List<DataPoint> nneighbors = obtainNeighbors(neighbor, points);
				if (nneighbors.size() > MIN_POINTS) {
					for (DataPoint nneighbor : nneighbors) {
						if (nneighbor.getClusterId() <= 0) {
							nneighbor.setClusterId(clusterId);
						}
					}
				}
			}
			//未被聚类的点归入当前聚类中
			if (neighbor.getClusterId() <= 0) {
				neighbor.setClusterId(clusterId);
			}
		}
	}
	
	public void cluster(List<DataPoint> points) {
		//clusterId初始为0表示未分类,分类后设置为一个正数,如果设置为-1表示噪声 
		int clusterId = 0;
		boolean flag = true;
		//所有点都被访问完成即停止遍历
		while (flag) {
			for (DataPoint point : points) {
				if (point.isAccessed()) {
					continue;
				}
				point.setAccessed(true);
				flag = true;
				List<DataPoint> neighbors = obtainNeighbors(point, points);
				System.out.println("----------------------------neighbors: " + neighbors.size());
				if (neighbors.size() >= MIN_POINTS) {
					//满足核心对象条件的点创建一个新簇
					clusterId = point.getClusterId() <= 0 ? (++clusterId) : point.getClusterId();
					System.out.println("--------------------------------clusterId: " + clusterId);
					mergeCluster(point, neighbors, clusterId, points);
				} else {
					//未满足核心对象条件的点暂时当作噪声处理
					if(point.getClusterId() <= 0) {
						 point.setClusterId(-1);
					}
				}
				flag = false;
			}
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
	
	//打印结果
	public void print(List<DataPoint> points) {
		Collections.sort(points, new Comparator<DataPoint>() {
			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return Integer.valueOf(o1.getClusterId()).compareTo(o2.getClusterId());
			}
		});
		for (DataPoint point : points) {
			System.out.println(point.getClusterId() + " - " + point.getCategory());
		}
	}

	public void run() {
		List<DataPoint> points = initData();
		cluster(points);
		print(points);
	}
	
	public static void main(String[] args) {
		new DocDBScanBuilder().run();
	}
	
}

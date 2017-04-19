package com.abc.datamining.modules.clustering.spectral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.abc.datamining.common.distance.CosineDistance;
import com.abc.datamining.modules.clustering.spectral.data.DataPoint;
import com.abc.datamining.modules.clustering.spectral.data.DataPointCluster;
import com.abc.datamining.common.document.Document;
import com.abc.datamining.common.document.DocumentLoader;
import com.abc.datamining.common.document.DocumentSet;
import com.abc.datamining.common.document.DocumentSimilarity;
import com.abc.datamining.common.document.DocumentUtils;
import com.abc.datamining.modules.clustering.spectral.data.Data;
import com.abc.datamining.utils.DistanceUtils;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class SpectralClustering {
	
	public static int DIMENSION = 30;
	
	public static double THRESHOLD = 0.01;

	public Data getInitData() {
		Data data = new Data();
		try {
			String path = SpectralClustering.class.getClassLoader()
					.getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSet(path);
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			DocumentUtils.calculateSimilarity(documents, new CosineDistance());
			Map<String, Map<String, Double>> nmap = new HashMap<String, Map<String, Double>>();
			Map<String, String> cmap = new HashMap<String, String>();
			for (Document document : documents) {
				String name = document.getName();
				cmap.put(name, document.getCategory());
				Map<String, Double> similarities = nmap.get(name);
				if (null == similarities) {
					similarities = new HashMap<String, Double>();
					nmap.put(name, similarities);
				}
				for (DocumentSimilarity similarity : document.getSimilarities()) {
					if (similarity.getDoc2().getName().equalsIgnoreCase(similarity.getDoc1().getName())) {
						similarities.put(similarity.getDoc2().getName(), 0.0);
					} else {
						similarities.put(similarity.getDoc2().getName(), similarity.getDistance());
					}
				}
			}
			String[] docnames = nmap.keySet().toArray(new String[0]);
			data.setRow(docnames);
			data.setColumn(docnames);
			data.setDocnames(docnames);
			int len = docnames.length;
			double[][] original = new double[len][len];
			for (int i = 0; i < len; i++) {
				Map<String, Double> similarities = nmap.get(docnames[i]);
				for (int j = 0; j < len; j++) {
					double distance = similarities.get(docnames[j]);
					original[i][j] = distance;
				}
			}
			data.setOriginal(original);
			data.setCmap(cmap);
			data.setNmap(nmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public double[][] getWByDistance(Data data) {
		Map<String, Map<String, Double>> nmap = data.getNmap();
		String[] docnames = data.getDocnames();
		int len = docnames.length;
		double[][] w = new double[len][len];
		for (int i = 0; i < len; i++) {
			Map<String, Double> similarities = nmap.get(docnames[i]);
			for (int j = 0; j < len; j++) {
				double distance = similarities.get(docnames[j]);
				w[i][j] = distance < THRESHOLD ? 1 : 0;
			}
		}
		return w;
	}
	
	public double[][] getWByKNearestNeighbors(Data data) {
		Map<String, Map<String, Double>> nmap = data.getNmap();
		String[] docnames = data.getDocnames();
		int len = docnames.length;
		double[][] w = new double[len][len];
		for (int i = 0; i < len; i++) {
			List<Map.Entry<String, Double>> similarities = 
					new ArrayList<Map.Entry<String, Double>>(nmap.get(docnames[i]).entrySet());
			sortSimilarities(similarities, DIMENSION);
			for (int j = 0; j < len; j++) {
				String name = docnames[j];
				boolean flag = false;
				for (Map.Entry<String, Double> entry : similarities) {
					if (name.equalsIgnoreCase(entry.getKey())) {
						flag = true;
						break;
					}
				}
				w[i][j] = flag ? 1 : 0;
			}
		}
		return w;
	}

	public double[][] getVerticalD(double[][] W) {
		int row = W.length;
		int column = W[0].length;
		double[][] d = new double[row][column];
		for (int j = 0; j < column; j++) {
			double sum = 0;
			for (int i = 0; i < row; i++) {
				sum += W[i][j];
			}
			d[j][j] = sum;
		}
		return d;
	}

	public double[][] getHorizontalD(double[][] W) {
		int row = W.length;
		int column = W[0].length;
		double[][] d = new double[row][column];
		for (int i = 0; i < row; i++) {
			double sum = 0;
			for (int j = 0; j < column; j++) {
				sum += W[i][j];
			}
			d[i][i] = sum;
		}
		return d;
	}
	
	public void sortSimilarities(List<Map.Entry<String, Double>> similarities, int k) {
		Collections.sort(similarities, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		while (similarities.size() > k) {
			similarities.remove(similarities.size() - 1);
		}
//		for (Map.Entry<String, Double> entry : similarities) {
//			System.out.println(entry.getKey() + "-" + entry.getValue());
//		}
//		System.out.println("------------------entry size: " + similarities.size());
	}

	public void print(double[][] values) {
		for (int i = 0, il = values.length; i < il; i++) {
			for (int j = 0, jl = values[0].length; j < jl; j++) {
				System.out.print(values[i][j] + "  ");
			}
			System.out.println("\n");
		}
	}

	// 随机生成中心点，并生成初始的K个聚类
	public List<DataPointCluster> genInitCluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		Random random = new Random();
		Set<String> categories = new HashSet<String>();
		while (clusters.size() < k) {
			DataPoint center = points.get(random.nextInt(points.size()));
			String category = center.getCategory();
			if (categories.contains(category))
				continue;
			categories.add(category);
			DataPointCluster cluster = new DataPointCluster();
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}

	// 将点归入到聚类中
	public void handleCluster(List<DataPoint> points,
			List<DataPointCluster> clusters, int iterNum) {
		System.out.println("iterNum: " + iterNum);
		for (DataPoint point : points) {
			DataPointCluster maxCluster = null;
			double maxDistance = Integer.MIN_VALUE;
			for (DataPointCluster cluster : clusters) {
				DataPoint center = cluster.getCenter();
				double distance = DistanceUtils.cosine(point.getValues(),
						center.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					maxCluster = cluster;
				}
			}
			if (null != maxCluster) {
				maxCluster.getDataPoints().add(point);
			}
		}
		// 终止条件定义为原中心点与新中心点距离小于一定阀值
		// 当然也可以定义为原中心点等于新中心点
		boolean flag = true;
		for (DataPointCluster cluster : clusters) {
			DataPoint center = cluster.getCenter();
			DataPoint newCenter = cluster.computeMediodsCenter();
			double distance = DistanceUtils.cosine(newCenter.getValues(),
					center.getValues());
			System.out.println("distaince: " + distance);
			if (distance > 0.5) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		System.out.println("--------------");
		if (!flag && iterNum < 25) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters, ++iterNum);
		}
	}

	public void kmeans(List<DataPoint> dataPoints) {
		List<DataPointCluster> clusters = genInitCluster(dataPoints, 4);
//		for (DataPointCluster cluster : clusters) {
//			System.out.println("center: " + cluster.getCenter().getCategory());
//		}
		handleCluster(dataPoints, clusters, 0);
		int success = 0, failure = 0;
		for (DataPointCluster cluster : clusters) {
			String category = cluster.getCenter().getCategory();
//			System.out.println("center: " + category + "--"
//					+ cluster.getDataPoints().size());
			for (DataPoint dataPoint : cluster.getDataPoints()) {
				String dpCategory = dataPoint.getCategory();
//				System.out.println(dpCategory);
				if (category.equals(dpCategory)) {
					success++;
				} else {
					failure++;
				}
			}
//			System.out.println("----------");
		}
		System.out.println("total: " + (success + failure) + " success: "
				+ success + " failure: " + failure);
	}

	public void buildOri() {
		Data data = getInitData();
		double[][] w = getWByDistance(data);
		System.out.println("-----w-----");
		print(w);
//		double[][] d = getVerticalD(w);
		double[][] d = getHorizontalD(w);
		System.out.println("-----d-----");
		print(d);
		Matrix W = new Matrix(w);
		Matrix D = new Matrix(d);
		Matrix L = D.minus(W);
		double[][] l = L.getArray();
		System.out.println("-----l-----");
		print(l);
//		SingularValueDecomposition svd = L.svd();
		// Matrix S = svd.getS();
		// double[][] s = S.getArray();
		// System.out.println("-----s-----");
		// print(s);
		EigenvalueDecomposition eig = L.eig();
//		 System.out.println("-----e-----");
//		 print(eig.getD().getArray());
//		 System.out.println("-----ev-----");
//		 print(eig.getV().getArray());
//		double[] singularValues = svd.getSingularValues();
//		int len = singularValues.length;
//		for (int i = 0; i < len; i++) {
//			singularValues[i] = singularValues[i] + singularValues[len - 1];
//			singularValues[len - 1] = singularValues[i]
//					- singularValues[len - 1];
//			singularValues[i] = singularValues[i] - singularValues[len - 1];
//		}
//		double[][] k = new double[len][len];
//		for (int i = 0; i < len; i++) {
//			for (int j = 0; j < len; j++) {
//				k[i][j] = (i < 50 && i == j) ? singularValues[i] : 0;
//			}
//		}
//		System.out.println("-----sv-----");
//		print(svd.getS().getArray());
//		Matrix K = new Matrix(k);
//		Matrix N = new Matrix(data.getOriginal());
//		Matrix NK = N.times(K);
//		System.out.println("-----nk-----");
//		double[][] nk = NK.getArray();
		double[][] nk = eig.getV().getArray();
		for (int i = 0, li = nk.length; i < li; i++) {
			for (int j = 0, lj = nk[0].length; j < lj; j++) {
				if (j == 0 || j > 51) {
					nk[i][j] = 0;
				}
			}
		}
		System.out.println("-----nk-----");
		print(nk);
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		for (int i = 0; i < nk.length; i++) {
			DataPoint dataPoint = new DataPoint();
			dataPoint.setCategory(data.getCmap().get(data.getColumn()[i]));
			dataPoint.setValues(nk[i]);
			dataPoints.add(dataPoint);
		}
		kmeans(dataPoints);
	}
	
	public void buildPrint() {
		Data data = getInitData();
		double[][] w = getWByKNearestNeighbors(data);
		System.out.println("-----w-----");
		print(w);
		double[][] d = getHorizontalD(w);
		System.out.println("-----d-----");
		print(d);
		Matrix W = new Matrix(w);
		Matrix D = new Matrix(d);
		Matrix L = D.minus(W);
		double[][] l = L.getArray();
		System.out.println("-----l-----");
		print(l);
		EigenvalueDecomposition eig = L.eig();
		double[][] nk = eig.getV().getArray();
		for (int i = 0, li = nk.length; i < li; i++) {
			for (int j = 0, lj = nk[0].length; j < lj; j++) {
				if (j == 0 || j > 51) {
					nk[i][j] = 0;
				}
			}
		}
		System.out.println("-----nk-----");
		print(nk);
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		for (int i = 0; i < nk.length; i++) {
			DataPoint dataPoint = new DataPoint();
			dataPoint.setCategory(data.getCmap().get(data.getColumn()[i]));
			dataPoint.setValues(nk[i]);
			dataPoints.add(dataPoint);
		}
		kmeans(dataPoints);
	}
	
	public void build() {
		Data data = getInitData();
		double[][] w = getWByKNearestNeighbors(data);
		double[][] d = getHorizontalD(w);
//		double[][] d = getVerticalD(w);
		Matrix W = new Matrix(w);
		Matrix D = new Matrix(d);
		Matrix L = D.minus(W);
		EigenvalueDecomposition eig = L.eig();
		double[][] v = eig.getV().getArray();
		double[][] vs = new double[v.length][DIMENSION];
		for (int i = 0, li = v.length; i < li; i++) {
			for (int j = 1, lj = DIMENSION; j <= lj; j++) {
//				if (j == 0 || j > 41) {
					vs[i][j-1] = v[i][j];
//				}
			}
		}
		Matrix V = new Matrix(vs);
		Matrix O = new Matrix(data.getOriginal());
	    double[][] t = O.times(V).getArray();
		print(t);
		System.out.println("t: " + t.length + "-" + t[0].length);
	    List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		for (int i = 0; i < t.length; i++) {
			DataPoint dataPoint = new DataPoint();
			dataPoint.setCategory(data.getCmap().get(data.getColumn()[i]));
			dataPoint.setValues(t[i]);
			dataPoints.add(dataPoint);
		}
		for (int n = 0; n < 10; n++) {
			kmeans(dataPoints);
		}
	}

	public static void main(String[] args) {
		new SpectralClustering().build();
	}
}

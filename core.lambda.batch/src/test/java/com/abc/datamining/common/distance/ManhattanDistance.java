package com.abc.datamining.common.distance;

import java.util.Iterator;
import java.util.Map;

import com.abc.datamining.common.vector.Vector;

/** 曼哈顿距离*/
public class ManhattanDistance implements IDistance {
	
	@Override
	public double distance(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}

	@Override
	public double distance(Vector<Double> p1, Vector<Double> p2) {
		if (p1.size() != p2.size()) {
			throw new RuntimeException("p1 size not equal p2 size");
		}
		Iterator<Vector.Element<Double>> v1Iter = p1.all().iterator();
		Iterator<Vector.Element<Double>> v2Iter = p2.all().iterator();
		double result = 0.0;
		while (v1Iter.hasNext()) {
			result += Math.abs(v1Iter.next().get() - v2Iter.next().get());
		}
		return result;
	}
	
	@Override
	public double distance(Map<String, Double> p1, Map<String, Double> p2) {
		double result = 0.0;
		for (Map.Entry<String, Double> entry : p1.entrySet()) {
			double v1 = entry.getValue();
			String k1 = entry.getKey();
			double v2 = null == p2.get(k1) ? 0 : p2.get(k1);
			result += Math.abs(v1 - v2);
		}
		return result;
	}

}

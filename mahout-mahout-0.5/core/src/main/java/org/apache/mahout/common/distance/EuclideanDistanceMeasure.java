package org.apache.mahout.common.distance;

import org.apache.mahout.math.Vector;


public class EuclideanDistanceMeasure extends SquaredEuclideanDistanceMeasure {
  
  @Override
  public double distance(Vector v1, Vector v2) {
    return Math.sqrt(super.distance(v1, v2));
  }
  
  @Override
  public double distance(double centroidLengthSquare, Vector centroid, Vector v) {
    return Math.sqrt(super.distance(centroidLengthSquare, centroid, v));
  }
}

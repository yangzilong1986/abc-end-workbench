package org.apache.mahout.cf.taste.impl.similarity;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.model.DataModel;

import com.google.common.base.Preconditions;

/**
 * 皮尔逊相关系数的相似度
 */
public final class PearsonCorrelationSimilarity extends AbstractSimilarity {

  /**
   * @throws IllegalArgumentException if {@link DataModel} does not have preference values
   */
  public PearsonCorrelationSimilarity(DataModel dataModel) throws TasteException {
    this(dataModel, Weighting.UNWEIGHTED);
  }

  /**
   * @throws IllegalArgumentException if {@link DataModel} does not have preference values
   */
  public PearsonCorrelationSimilarity(DataModel dataModel, Weighting weighting) throws TasteException {
    super(dataModel, weighting, true);
    Preconditions.checkArgument(dataModel.hasPreferenceValues(), "DataModel doesn't have preference values");
  }
  
  @Override
  double computeResult(int n, double sumXY, double sumX2, double sumY2, double sumXYdiff2) {
    if (n == 0) {
      return Double.NaN;
    }
    // Note that sum of X and sum of Y don't appear here since they are assumed to be 0;
    // the data is assumed to be centered.
    double denominator = Math.sqrt(sumX2) * Math.sqrt(sumY2);
    if (denominator == 0.0) {
      // One or both parties has -all- the same ratings;
      // can't really say much similarity under this measure
      return Double.NaN;
    }
    return sumXY / denominator;
  }
  
}

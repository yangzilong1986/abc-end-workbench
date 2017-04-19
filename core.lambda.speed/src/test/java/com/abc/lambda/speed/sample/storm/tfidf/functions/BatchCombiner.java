package com.abc.lambda.speed.sample.storm.tfidf.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

public class BatchCombiner extends BaseFunction {

    Logger LOG = LoggerFactory.getLogger(BatchCombiner.class);
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        try {
            //当前小时的数据
            double d_rt = (double) tuple.getLongByField("d_rt");
            double df_rt = (double) tuple.getLongByField("df_rt");
            double tf_rt = (double) tuple.getLongByField("tf_rt");
            //以前的所有数据
            double d_batch = (double) tuple.getLongByField("d_batch");
            double df_batch = (double) tuple.getLongByField("df_batch");
            double tf_batch = (double) tuple.getLongByField("tf_batch");

            LOG.debug("Combining! d_rt=" + d_rt + "df_rt=" + df_rt + "tf_rt="
                    + tf_rt + "d_batch=" + d_batch + "df_batch=" + df_batch
                    + "tf_batch=" + tf_batch);

            collector.emit(new Values(tf_rt + tf_batch, d_rt + d_batch, df_rt
                    + df_batch));
        } catch (Exception e) {
        }

    }

}

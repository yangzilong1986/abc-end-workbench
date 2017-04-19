package com.abc.lambda.speed.sample.storm.tfidf.functions;

import java.util.Map;

//import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.LocalDRPC;
import org.apache.storm.generated.DRPCExecutionException;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.DRPCClient;

public class TfidfExpression extends BaseFunction {

    Logger LOG = LoggerFactory.getLogger(TfidfExpression.class);
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        try {
            double d = (double)tuple.getLongByField("d");
            double df = (double)tuple.getLongByField("df");
            double tf = (double) tuple.getLongByField("tf");
            LOG.debug("d=" + d + "df=" + df + "tf="+ tf);
            double tfidf = tf * Math.log(d / (1 + df));
            LOG.debug("Emitting new TFIDF(term,Document): ("
                    + tuple.getStringByField("term") + ","
                    + tuple.getStringByField("documentId") + ") = " + tfidf);
            collector.emit(new Values(tfidf));
        } catch (Exception e) {}

    }

}
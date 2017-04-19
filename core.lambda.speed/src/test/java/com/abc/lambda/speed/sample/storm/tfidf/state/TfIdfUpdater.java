package com.abc.lambda.speed.sample.storm.tfidf.state;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.BaseStateUpdater;
import org.apache.storm.trident.state.map.SnapshottableMap;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

@SuppressWarnings("rawtypes")
public class TfIdfUpdater extends BaseStateUpdater<SnapshottableMap> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "unchecked" })
    @Override
    public void updateState(SnapshottableMap state, List<TridentTuple> tuples,
                            TridentCollector collector) {
        List<List<Object>> keys = new LinkedList<List<Object>>();
        List<List<Object>> values = new LinkedList<List<Object>>();

        for(TridentTuple tuple: tuples){
            List<Object> k = new ArrayList<Object>();
            List<Object> v = new ArrayList<Object>();
            k.add(tuple.getValueByField("documentId"));
            k.add(tuple.getValueByField("term"));
            v.add(tuple.getValueByField("tfidf"));
            keys.add(k);
            values.add(v);
        }
        state.multiPut(keys, values);

        for(TridentTuple tuple: tuples){
            collector.emit(new Values(tuple.getValueByField("documentId"),
                    tuple.getValueByField("term"),
                    tuple.getValueByField("tfidf")));
        }

    }



}

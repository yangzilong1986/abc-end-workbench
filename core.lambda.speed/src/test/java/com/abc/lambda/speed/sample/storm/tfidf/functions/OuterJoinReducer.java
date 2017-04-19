package com.abc.lambda.speed.sample.storm.tfidf.functions;

import java.util.Map;

import org.apache.storm.trident.operation.MultiReducer;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentMultiReducerContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

public class OuterJoinReducer implements MultiReducer<OuterJoinState> {
    private static final long serialVersionUID = 1L;

    @Override
    public void prepare(Map conf, TridentMultiReducerContext context) {

    }

    @Override
    public OuterJoinState init(TridentCollector collector) {
        return new OuterJoinState();
    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public void execute(OuterJoinState state, int streamIndex,
                        TridentTuple input, TridentCollector collector) {
        state.addValues(streamIndex, input);
    }

    @Override
    public void complete(OuterJoinState state,
                         TridentCollector collector) {
        for(Values vals : state.join()){
            collector.emit(vals);
        }
    }

}

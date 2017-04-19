package com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.state;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.state.StateUpdater;
import org.apache.storm.trident.tuple.TridentTuple;

import java.util.List;
import java.util.Map;

public class DruidStateUpdater implements StateUpdater<DruidState> {
    private static final long serialVersionUID = 1L;
//    private static final Logger LOG = LoggerFactory.getLogger(DruidStateUpdater.class);

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map conf, TridentOperationContext context) {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void updateState(DruidState state, List<TridentTuple> tuples, TridentCollector collector) {
        //LOG.info("Updating [" + state + "]");
//        for (TridentTuple tuple : tuples) {
//            FixMessageDto message = (FixMessageDto) tuple.getValue(0);
//            state.aggregateMessage(message);
//        }
    }
}

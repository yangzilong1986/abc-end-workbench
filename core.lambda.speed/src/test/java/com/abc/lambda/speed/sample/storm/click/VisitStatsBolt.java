package com.abc.lambda.speed.sample.storm.click;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class VisitStatsBolt extends BaseRichBolt {

    private OutputCollector collector;

    private int total = 0;
    private int uniqueCount = 0;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        boolean unique = Boolean.parseBoolean(tuple.getStringByField(ClientFields.UNIQUE));
        total++;
        if(unique)uniqueCount++;
        collector.emit(new Values(total,uniqueCount));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new org.apache.storm.tuple.Fields(ClientFields.TOTAL_COUNT,
                ClientFields.TOTAL_UNIQUE));
    }
}

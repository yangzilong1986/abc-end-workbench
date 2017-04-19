package com.abc.lambda.speed.sample.storm.click;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeographyBolt extends BaseRichBolt {


    private IPResolver resolver;

    private OutputCollector collector;

    public GeographyBolt(IPResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String ip = tuple.getStringByField(ClientFields.IP);
        JSONObject json = resolver.resolveIP(ip);
        String city = (String) json.get(ClientFields.CITY);
        String country = (String) json.get(ClientFields.COUNTRY_NAME);
        collector.emit(new Values(country, city));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(ClientFields.COUNTRY, ClientFields.CITY));
    }
}

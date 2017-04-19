package com.abc.lambda.speed.sample.storm.click;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class RepeatVisitBolt extends BaseRichBolt {

    private OutputCollector collector;

    private Jedis jedis;
    private String host;
    private int port;

    @Override
    public void prepare(Map conf, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        host = conf.get(Conf.REDIS_HOST_KEY).toString();
        port = Integer.valueOf(conf.get(Conf.REDIS_PORT_KEY).toString());
        connectToRedis();
    }

    private void connectToRedis() {
        jedis = new Jedis(host, port);
        jedis.connect();
    }

    public boolean isConnected(){
        if(jedis == null)
            return false;
        return jedis.isConnected();
    }

    @Override
    public void execute(Tuple tuple) {
        String ip = tuple.getStringByField(ClientFields.IP);
        String clientKey = tuple.getStringByField(ClientFields.CLIENT_KEY);
        String url = tuple.getStringByField(ClientFields.URL);
        String key = url + ":" + clientKey;
        String value = jedis.get(key);
        if(value == null){
            jedis.set(key, "visited");
            collector.emit(new Values(clientKey, url, Boolean.TRUE.toString()));
        }  else {
            collector.emit(new Values(clientKey, url, Boolean.FALSE.toString()));
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new org.apache.storm.tuple.Fields(ClientFields.CLIENT_KEY,
                ClientFields.URL,ClientFields.UNIQUE));
    }
}

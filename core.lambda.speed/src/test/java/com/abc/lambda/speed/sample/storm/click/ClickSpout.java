package com.abc.lambda.speed.sample.storm.click;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;

import java.util.Map;
import org.apache.log4j.Logger;

public class ClickSpout extends BaseRichSpout {

    public static Logger LOG = Logger.getLogger(ClickSpout.class);

    private Jedis jedis;
    private String host;
    private int port;
    private SpoutOutputCollector collector;


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(ClientFields.IP,
                ClientFields.URL,
                ClientFields.CLIENT_KEY));
    }

    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        host = conf.get(Conf.REDIS_HOST_KEY).toString();
        port = Integer.valueOf(conf.get(Conf.REDIS_PORT_KEY).toString());
        this.collector = spoutOutputCollector;
        connectToRedis();
    }

    private void connectToRedis() {
        jedis = new Jedis(host, port);
    }

    @Override
    public void nextTuple() {
        String content = jedis.rpop("count");
        if(content==null || "nil".equals(content)) {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        } else {
            JSONObject obj=(JSONObject) JSONValue.parse(content);
            String ip = obj.get(ClientFields.IP).toString();
            String url = obj.get(ClientFields.URL).toString();
            String clientKey = obj.get(ClientFields.CLIENT_KEY).toString();
            collector.emit(new Values(ip,url,clientKey));
        }
    }
}

package com.abc.lambda.speed.sample.storm.tfidf.spout;

import java.util.Map;

import com.abc.lambda.speed.sample.storm.tfidf.Conf;
import redis.clients.jedis.Jedis;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class TweetURLSpout extends BaseRichSpout {

    private Jedis jedis;
    private String host;
    private int port;
    private SpoutOutputCollector collector;


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("url"));
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
        String url = jedis.rpop("url");
        if(url==null) {
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        } else {
            collector.emit(new Values(url));
        }
    }

}

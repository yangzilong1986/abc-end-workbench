package com.abc.lambda.speed.sample.storm.tfidf.bolt;

import java.util.Map;

import redis.clients.jedis.Jedis;
import twitter4j.Status;
import twitter4j.URLEntity;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class PublishURLBolt extends BaseRichBolt{

    Jedis jedis;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        jedis = new Jedis("localhost");

    }

    @Override
    public void execute(Tuple input) {
        Status ret = (Status) input.getValue(0);
        URLEntity[] urls = ret.getURLEntities();
        for(int i = 0; i < urls.length;i++){
            jedis.rpush("url", urls[i].getURL().trim());
        }


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }
}

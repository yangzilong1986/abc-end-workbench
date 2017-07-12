package com.abc.lambda.speed.sample.storm.simple;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;



public class SimpleBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    public void execute(Tuple input,BasicOutputCollector collector) {
        try {
            String msg = input.getString(0);
            if (msg != null){
                //System.out.println("msg="+msg);
                collector.emit(new Values(msg + "msg is processed!"));
            }
                
        } catch (Exception e) {
            e.printStackTrace(); 
        }

    }

    public void declareOutputFields(
            OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("info"));

    }

}

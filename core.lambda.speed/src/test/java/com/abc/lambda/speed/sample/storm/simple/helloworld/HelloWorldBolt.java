package com.abc.lambda.speed.sample.storm.simple.helloworld;

import org.apache.log4j.Logger;

//import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

//import org.apache.storm.topology.OutputFieldsDeclarer;

//import org.apache.storm.tuple.Fields;
//import org.apache.storm.tuple.Tuple;
//import org.apache.storm.tuple.Values;


public class HelloWorldBolt extends BaseBasicBolt {

    public static Logger LOG = Logger.getLogger(HelloWorldBolt.class);

    private static final long serialVersionUID = -841805977046116528L;

    private int myCount = 0;

//    @Override
//    public void prepare(Map stormConf, TopologyContext context,
//                        OutputCollector collector) {
//    }

//    @Override
//    public void execute(Tuple input) {
//        String test = input.getStringByField("sentence");
//        if(test == "Hello World"){
//            myCount++;
//            System.out.println("Found a Hello World! My Count is now: " + Integer.toString(myCount));
//            LOG.debug("Found a Hello World! My Count is now: " + Integer.toString(myCount));
//        }
//    }

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

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("myCount"));

    }


}

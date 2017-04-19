package com.abc.lambda.speed.sample.storm.simple.helloworld;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class HelloWorldTopology {

    /**
     * @param args
     * @throws Exception
     * @throws
     */
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("randomHelloWorld", new HelloWorldSpout(), 2);
        builder.setBolt("HelloWorldBolt", new HelloWorldBolt(), 10)
                .shuffleGrouping("randomHelloWorld");

        Config conf = new Config();
        conf.setDebug(true);

        if(args!=null && args.length > 0) {
            conf.setNumWorkers(20);

            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {
            conf.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(10000);

//            cluster.killTopology("test");
//            cluster.shutdown();
        }

    }

}

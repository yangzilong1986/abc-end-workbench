package com.abc.lambda.speed.sample.storm.simple.wordcount;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

        
public class TopologyMain {
     public static void main(String[] args) throws InterruptedException {
     
        //Topology definition
        TopologyBuilder builder = new TopologyBuilder();       
        builder.setSpout("WordReader",new WordReader(),1);
        builder.setBolt("WordNormalizer", new WordNormalizer()).shuffleGrouping("WordReader");
        builder.setBolt("WordCounter", new WordCounter(),1).fieldsGrouping("WordNormalizer", new Fields("word"));

        //Configuration
        Config conf = new Config();
        conf.put("wordsFile", "D:\\DevN\\abc-end-workbench\\core.lambda.speed\\src\\test\\resources\\words.txt");
        conf.setDebug(false);
        //Topology run
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
//        Thread.sleep(1000);
//        cluster.shutdown();
    }
}
package com.abc.lambda.speed.sample.storm.rftopology;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.apache.storm.kafka.KafkaConfig;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.builtin.Debug;
import org.apache.storm.trident.operation.builtin.FilterNull;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

//import com.github.quintona.KafkaState;
//import com.github.quintona.KafkaStateUpdater;

public class OrderManagementTopology {

    public static class CoerceInFunction extends BaseFunction {

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {

            String text = new String(tuple.getBinary(0));
            JSONArray array = (JSONArray) JSONValue.parse(text);
            List<Object> values = new ArrayList<Object>(array.size());
            String id = (String) array.get(array.size() - 1);
            array.remove(array.size() - 1);
            for(Object obj : array){
                values.add(Double.parseDouble((String)obj));
            }
            values.add(id);

            if(array.size() > 0){
                collector.emit(new Values(values.toArray()));
            }
        }
    }

    public static class CoerceOutFunction extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            JSONObject obj = new JSONObject();
            obj.put("order-id", tuple.getStringByField("order-id"));
            obj.put("dispatch-to", tuple.getStringByField("dispatch-to"));
            collector.emit(new Values(obj.toJSONString()));
        }
    }

    public static class EnrichFunction extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            String prediction = tuple.getStringByField("prediction");
            if("0".equals(prediction))
                collector.emit(new Values("Hub1"));
            if("1".equals(prediction))
                collector.emit(new Values("Hub2"));
        }
    }

    public static List<String> getFieldNames(int properyCount){
        List<String> names = new ArrayList<String>(properyCount);
        for(int i = 1; i <= properyCount; i++){
            names.add("Value" + Integer.toString(i));
        }
        return names;
    }

    public static TridentTopology makeTopology(int properyCount) throws IOException {
        TridentTopology topology = new TridentTopology();

        TridentKafkaConfig spoutConfig = new TridentKafkaConfig(null, "orders");

        List<String> valueNames = getFieldNames(properyCount);
        List<String> allFields = new ArrayList<String>(1);
        allFields.addAll(valueNames);
        allFields.add("order-id");

        topology.newStream("kafka",
                new TransactionalTridentKafkaSpout(spoutConfig))
                .each(new Fields("bytes"), new CoerceInFunction(),new Fields(allFields))
//                .each(new Fields(valueNames), new ClassifierFunction("/usr/local/random_forest.xml"),
//                        new Fields("prediction"))
                .each(new Fields("prediction"),
                        new Debug("Prediction"))
                .each(new Fields("prediction"),
                        new EnrichFunction(), new Fields("dispatch-to"))
                .each(new Fields("order-id", "dispatch-to"),
                        new CoerceOutFunction(),new Fields("message"));
//                .partitionPersist(KafkaState.transactional("order-output",
//                        new KafkaState.Options()), new Fields("message"),
//                        new KafkaStateUpdater("message"), new Fields("message"));

        return topology;
    }

    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        conf.setDebug(true);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            //TODO: get count from the args.
            StormSubmitter
                    .submitTopology(args[0], conf, makeTopology(10).build());
        } else {
            conf.setMaxTaskParallelism(3);
            conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[]{"127.0.0.1"}));
            conf.put(Config.STORM_ZOOKEEPER_PORT, 2181);
            conf.put(Config.STORM_ZOOKEEPER_ROOT, "/storm");
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("transactional-topology", conf,
                    makeTopology(10).build());
        }
    }
}
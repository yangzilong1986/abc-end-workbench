package com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.topology;

import com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.operator.MessageTypeFilter;
import com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.spout.FixEventSpout;
import com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.state.DruidStateFactory;
import com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.state.DruidStateUpdater;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.tuple.Fields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;

public class FinancialAnalyticsTopology {
    private static final Logger LOG = LoggerFactory.getLogger(FinancialAnalyticsTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();
        FixEventSpout spout = new FixEventSpout();
        Stream inputStream = topology.newStream("message", spout);

        inputStream.each(new Fields("message"), new MessageTypeFilter())
                .partitionPersist(new DruidStateFactory(), new Fields("message"), new DruidStateUpdater());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
//        LOG.register();

        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");

        cluster.submitTopology("financial", conf, buildTopology());
        LOG.info("Topology submitted.");

        Thread.sleep(600000);
    }
}

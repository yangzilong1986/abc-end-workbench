package com.abc.lambda.speed.sample.storm.log;

import com.abc.lambda.speed.sample.storm.log.model.LogEntry;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.util.Map;

public class IndexerBolt extends BaseRichBolt {

    private Client client;
    public static Logger LOG = Logger.getLogger(LogRulesBolt.class);
    private OutputCollector collector;

    public static final String INDEX_NAME = "logstorm";
    public static final String INDEX_TYPE = "logentry";

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
        Node node;
        if((Boolean)stormConf.get(org.apache.storm.Config.TOPOLOGY_DEBUG) == true){
            node = NodeBuilder.nodeBuilder().local(true).node();
        } else {
            String clusterName = (String) stormConf.get(Conf.ELASTIC_CLUSTER_NAME);
            if(clusterName == null)
                clusterName = Conf.DEFAULT_ELASTIC_CLUSTER;
            node = NodeBuilder.nodeBuilder().clusterName(clusterName).node();
        }
        client = node.client();
    }

    @Override
    public void execute(Tuple input) {
        LogEntry entry = (LogEntry)input.getValueByField(FieldNames.LOG_ENTRY);
        if(entry == null){
            LOG.fatal( "Received null or incorrect value from tuple" );
            return;
        }
        String toBeIndexed = entry.toJSON().toJSONString();
        IndexResponse response = client.prepareIndex(INDEX_NAME,INDEX_TYPE)
                .setSource(toBeIndexed)
                .execute().actionGet();
        if(response == null)
            LOG.error("Failed to index Tuple: " + input.toString());
        else{
            if(response.getId() == null)
                LOG.error("Failed to index Tuple: " + input.toString());
            else{
                LOG.debug("Indexing success on Tuple: " + input.toString());
                collector.emit(new Values(entry,response.getId()));
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(FieldNames.LOG_ENTRY, FieldNames.LOG_INDEX_ID));
    }

}

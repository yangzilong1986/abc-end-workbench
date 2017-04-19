package com.abc.lambda.speed.sample.storm.log;

import java.util.Map;

import org.apache.log4j.Logger;
//import org.drools.KnowledgeBase;
//import org.drools.KnowledgeBaseFactory;
//import org.drools.builder.KnowledgeBuilder;
//import org.drools.builder.KnowledgeBuilderFactory;
//import org.drools.builder.ResourceType;
//import org.drools.io.ResourceFactory;
//import org.drools.runtime.StatelessKnowledgeSession;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import com.abc.lambda.speed.sample.storm.log.model.LogEntry;

public class LogRulesBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;
    public static Logger LOG = Logger.getLogger(LogRulesBolt.class);
//    private StatelessKnowledgeSession ksession;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
        //TODO: load the rule definitions from an external agent instead of the classpath.
//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        kbuilder.add( ResourceFactory.newClassPathResource( "/Syslog.drl",
//                getClass() ), ResourceType.DRL );
//        if ( kbuilder.hasErrors() ) {
//            LOG.error( kbuilder.getErrors().toString() );
//        }
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
//        ksession = kbase.newStatelessKnowledgeSession();
    }

    @Override
    public void execute(Tuple input) {
        LogEntry entry = (LogEntry)input.getValueByField(FieldNames.LOG_ENTRY);
        if(entry == null){
            LOG.fatal( "Received null or incorrect value from tuple" );
            return;
        }
//        ksession.execute( entry );
        if(!entry.isFilter()){
            LOG.debug("Emitting from Rules Bolt");
            collector.emit(new Values(entry));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(FieldNames.LOG_ENTRY));
    }

}

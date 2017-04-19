package com.abc.lambda.speed.sample.storm.log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import com.abc.lambda.speed.sample.storm.log.model.LogEntry;

public class VolumeCountingBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;
    public static Logger LOG = Logger.getLogger(VolumeCountingBolt.class);
    private OutputCollector collector;

    public static final String FIELD_ROW_KEY = "RowKey";
    public static final String FIELD_COLUMN = "Column";
    public static final String FIELD_INCREMENT = "IncrementAmount";

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
    }

    public static Long getMinuteForTime(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    @Override
    public void execute(Tuple input) {
        LogEntry entry = (LogEntry) input.getValueByField(FieldNames.LOG_ENTRY);
        collector.emit(new Values(getMinuteForTime(entry.getTimestamp()), entry.getSource(),1L));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(FIELD_ROW_KEY, FIELD_COLUMN, FIELD_INCREMENT));
    }

}
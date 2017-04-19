package com.abc.lambda.speed.sample.storm.tfidf.state;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.storm.utils.Time;

//import trident.cassandra.CassandraState.Options;
//import trident.cassandra.RowKeyStrategy;

/**
 * 滚动窗口的实现
 */
public class TimeBasedRowStrategy implements Serializable {

    private static final long serialVersionUID = 6981400531506165681L;

//    public <T> String getRowKey(List<List<Object>> keys, Options<T> options) {
//        return options.rowKey + StateUtils.formatHour(new Date(Time.currentTimeMillis()));
//    }

}

package com.abc.lambda.speed.sample.storm.blueprints.druid.storm.trident.operator;

import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.tuple.TridentTuple;

public class MessageTypeFilter extends BaseFilter {
    private static final long serialVersionUID = 1L;
//    private static final Logger LOG = LoggerFactory.getLogger(MessageTypeFilter.class);

    @Override
    public boolean isKeep(TridentTuple tuple) {
//        FixMessageDto message = (FixMessageDto) tuple.getValue(0);
//        if (message.msgType.equals("8") && message.price > 0) {
//            return true;
//        }
        return false;
    }
}
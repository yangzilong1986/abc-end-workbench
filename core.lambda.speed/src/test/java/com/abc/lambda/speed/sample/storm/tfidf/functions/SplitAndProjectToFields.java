package com.abc.lambda.speed.sample.storm.tfidf.functions;
import org.apache.storm.tuple.Values;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;


public class SplitAndProjectToFields extends BaseFunction {

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        Values vals = new Values();
        for(String word: tuple.getString(0).split(" ")) {
            if(word.length() > 0) {
                vals.add(word);
            }
        }
        collector.emit(vals);

    }



}

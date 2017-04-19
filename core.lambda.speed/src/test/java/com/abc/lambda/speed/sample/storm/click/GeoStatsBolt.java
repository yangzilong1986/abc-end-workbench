package com.abc.lambda.speed.sample.storm.click;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeoStatsBolt extends BaseRichBolt {

    private class CountryStats {

        private int countryTotal = 0;

        private static final int COUNT_INDEX = 0;
        private static final int PERCENTAGE_INDEX = 1;
        private String countryName;

        public CountryStats(String countryName){
            this.countryName = countryName;
        }

        private Map<String, List<Integer>> cityStats = new HashMap<String, List<Integer>>();

        public void cityFound(String cityName){
            countryTotal++;
            if(cityStats.containsKey(cityName)){
                cityStats.get(cityName).set(COUNT_INDEX, cityStats.get(cityName).get(COUNT_INDEX).intValue() + 1);
            } else {
                List<Integer> list = new LinkedList<Integer>();
                list.add(1);
                list.add(0);
                cityStats.put(cityName, list);
            }

            double percent = (double)cityStats.get(cityName).get(COUNT_INDEX)/(double)countryTotal;
            cityStats.get(cityName).set(PERCENTAGE_INDEX, (int)percent);
        }

        public int getCountryTotal(){return countryTotal;}

        public int getCityTotal(String cityName){
            return cityStats.get(cityName).get(COUNT_INDEX).intValue();
        }

        public String toString(){
            return "Total Count for " + countryName + " is " + Integer.toString(countryTotal) + "\n"
                    +  "Cities:  " + cityStats.toString();
        }
    }

    private OutputCollector collector;
    private Map<String, CountryStats> stats = new HashMap<String, CountryStats>();
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String country = tuple.getStringByField(ClientFields.COUNTRY);
        String city = tuple.getStringByField(ClientFields.CITY);
        if(!stats.containsKey(country)){
            stats.put(country, new CountryStats(country));
        }
        stats.get(country).cityFound(city);
        collector.emit(new Values(country, stats.get(country).getCountryTotal(), city, stats.get(country).getCityTotal(city)));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new org.apache.storm.tuple.Fields(ClientFields.COUNTRY,
                ClientFields.COUNTRY_TOTAL,
                ClientFields.CITY,
                ClientFields.CITY_TOTAL));
    }
}

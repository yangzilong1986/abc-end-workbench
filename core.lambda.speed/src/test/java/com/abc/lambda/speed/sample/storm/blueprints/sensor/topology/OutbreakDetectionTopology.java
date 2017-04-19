package com.abc.lambda.speed.sample.storm.blueprints.sensor.topology;

import com.abc.lambda.speed.sample.storm.blueprints.sensor.operator.*;
import com.abc.lambda.speed.sample.storm.blueprints.sensor.spout.DiagnosisEventSpout;
import com.abc.lambda.speed.sample.storm.blueprints.sensor.state.OutbreakTrendFactory;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.tuple.Fields;

import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;

public class OutbreakDetectionTopology {

    public static StormTopology buildTopology() {
        TridentTopology topology = new TridentTopology();
        DiagnosisEventSpout spout = new DiagnosisEventSpout();
        //发射疾病事件
        Stream inputStream = topology.newStream("event", spout);
        //过滤不关心的疾病
        inputStream.each(new Fields("event"), new DiseaseFilter())
                //赋值对应的城市名称
                .each(new Fields("event"), new CityAssignment(), new Fields("city"))
                //赋值表示时间的时间戳
                .each(new Fields("event", "city"), new HourAssignment(), new Fields("hour", "cityDiseaseHour"))
                .groupBy(new Fields("cityDiseaseHour"))
                //持久化数据
                .persistentAggregate(new OutbreakTrendFactory(), new Count(), new Fields("count")).newValuesStream()
                //阀值判断
                .each(new Fields("cityDiseaseHour", "count"), new OutbreakDetector(), new Fields("alert"))
                //报警
                .each(new Fields("alert"), new DispatchAlert(), new Fields());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("cdc", conf, buildTopology());
        Thread.sleep(200000);
        cluster.shutdown();
    }
}

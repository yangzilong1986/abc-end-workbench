package com.abc.lambda.speed.sample.storm.tfidf.state;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StateUtils {

    public static String formatHour(Date date){
        return new SimpleDateFormat("yyyyMMddHH").format(date);
    }

}
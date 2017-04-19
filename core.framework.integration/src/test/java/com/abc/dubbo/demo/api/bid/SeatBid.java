package com.abc.dubbo.demo.api.bid;

import java.io.Serializable;

/**
 * @author lishen
 */
public class SeatBid implements Serializable {

    private String seat;

    private String group;

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

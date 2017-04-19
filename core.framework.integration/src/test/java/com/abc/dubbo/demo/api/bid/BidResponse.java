package com.abc.dubbo.demo.api.bid;

import java.io.Serializable;
import java.util.List;

/**
 * @author lishen
 */
public class BidResponse implements Serializable {

    private String id;
    private List<SeatBid> seatBids;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SeatBid> getSeatBids() {
        return seatBids;
    }

    public void setSeatBids(List<SeatBid> seatBids) {
        this.seatBids = seatBids;
    }
}

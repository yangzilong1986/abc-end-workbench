package com.abc.dubbo.provider.demo.bid;

import com.abc.dubbo.demo.api.bid.BidRequest;
import com.abc.dubbo.demo.api.bid.BidResponse;
import com.abc.dubbo.demo.api.bid.BidService;
import com.abc.dubbo.demo.api.bid.SeatBid;

import java.util.ArrayList;
import java.util.List;

public class BidServiceImpl implements BidService {

    public BidResponse bid(BidRequest request) {
        BidResponse response = new BidResponse();

        response.setId("abc");

        SeatBid seatBid = new SeatBid();
        seatBid.setGroup("group");
        seatBid.setSeat("seat");
        List<SeatBid> seatBids = new ArrayList<SeatBid>(1);
        seatBids.add(seatBid);

        response.setSeatBids(seatBids);

        return response;
    }

    public void throwNPE() throws NullPointerException {
        throw new NullPointerException();
    }

}
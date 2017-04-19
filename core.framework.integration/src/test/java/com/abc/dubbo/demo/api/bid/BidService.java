package com.abc.dubbo.demo.api.bid;

public interface BidService {

    BidResponse bid(BidRequest request);

    void throwNPE() throws NullPointerException;
}
/**
 * Copyright 1999-2014 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abc.dubbo.demo.api;

import com.abc.dubbo.demo.api.bid.BidRequest;
import com.abc.dubbo.demo.api.bid.BidResponse;
import com.abc.dubbo.demo.api.bid.Device;
import com.abc.dubbo.demo.api.bid.SeatBid;
import com.alibaba.dubbo.common.serialize.support.SerializationOptimizer;
import com.abc.dubbo.demo.api.bid.Geo;
import com.abc.dubbo.demo.api.bid.Impression;
import com.abc.dubbo.demo.api.user.User;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class must be accessible from both the provider and consumer
 *
 * @author lishen
 */
public class SerializationOptimizerImpl implements SerializationOptimizer {

    public Collection<Class> getSerializableClasses() {
        List<Class> classes = new LinkedList<Class>();
        classes.add(BidRequest.class);
        classes.add(BidResponse.class);
        classes.add(Device.class);
        classes.add(Geo.class);
        classes.add(Impression.class);
        classes.add(SeatBid.class);
        classes.add(User.class);
        return classes;
    }
}

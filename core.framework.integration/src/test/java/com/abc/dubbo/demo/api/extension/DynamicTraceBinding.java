package com.abc.dubbo.demo.api.extension;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

/**
 * @author lishen
 */
public class DynamicTraceBinding implements DynamicFeature {

    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        context.register(DynamicTraceInterceptor.class);
    }
}
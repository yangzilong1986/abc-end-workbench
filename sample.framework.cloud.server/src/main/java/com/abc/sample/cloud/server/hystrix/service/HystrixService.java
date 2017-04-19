package com.abc.sample.cloud.server.hystrix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HystrixService {

    @Autowired
    private CallDependencyService dependencyService;
    public String callDependencyService() {
        return dependencyService.mockGetUserInfo();
    }
}

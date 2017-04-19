package com.abc.sample.cloud.cluster.service.hello;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@RestController
public class Node02 {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Node02.class).web(true).run(args);
    }

    @Autowired
    private HelloService service;

    @RequestMapping("/")
    public String hello() {
        return this.service.hello();
    }


    @Component
    public static class HelloService {

        @HystrixCommand(fallbackMethod="fallback")
        public String hello() {
            return "Hello World";
        }
        public String fallback() {
            return "Fallback";
        }
    }
}


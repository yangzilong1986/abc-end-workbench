package com.abc.sample.cloud.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@ComponentScan
@RestController
@RefreshScope
@EnableEurekaClient
@EnableDiscoveryClient
public class Application {

    @Value("${name:World!}") String name ;

    @RequestMapping("/")
    public String home(){
        return "Hello " + name;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}


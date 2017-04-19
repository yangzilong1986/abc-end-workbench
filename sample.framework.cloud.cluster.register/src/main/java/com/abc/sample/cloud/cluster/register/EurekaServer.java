package com.abc.sample.cloud.cluster.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration
@EnableEurekaServer
public class EurekaServer {

    public static void main(String[] args) {
//        new SpringApplicationBuilder(EurekaServer.class).properties(
//                "spring.config.name:eureka", "logging.level.com.netflix.discovery:OFF")
//                .run(args);
        SpringApplication.run(EurekaServer.class,args);

    }

}


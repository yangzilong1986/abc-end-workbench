package com.abc.sample.cloud.provier.bs.order;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 主类中通过加上@EnableDiscoveryClient注解，
 * 该注解能激活Eureka中的DiscoveryClient实现，才能实现Controller中对服务信息的输出
 */
@EnableDiscoveryClient
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OrderApplication.class).web(true).run(args);
    }
}
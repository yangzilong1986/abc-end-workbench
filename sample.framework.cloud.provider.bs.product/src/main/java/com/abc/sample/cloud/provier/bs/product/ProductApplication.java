package com.abc.sample.cloud.provier.bs.product;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProductApplication.class).web(true).run(args);
    }

}
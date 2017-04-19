package com.abc.sample.cloud.server;
/**
 * 微服务架构就是将一个完整的应用从数据存储开始垂直拆分成多个不同的服务，
 * 每个服务都能独立部署、独立维护、独立扩展，服务与服务间通过诸如RESTful API的方式互相调用。
 * Spring Cloud Netflix，主要内容是对Netflix公司一系列开源产品的包装，
 * 它为Spring Boot应用提供了自配置的Netflix OSS整合。通过一些简单的注解，
 * 开发者就可以快速的在应用中配置一下常用模块并构建庞大的分布式系统。
 * 它主要提供的模块包括：服务发现（Eureka），断路器（Hystrix），
 * 智能路有（Zuul），客户端负载均衡（Ribbon）等。
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * 通过@EnableEurekaServer注解启动一个服务注册中心提供给其他应用进行
 */
@SpringBootApplication
//@EnableConfigServer
@EnableEurekaServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}

package com.abc.dubbo.provider.demo.setup;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootApplication
//@ComponentScan(basePackages = {"com.abc.dubbo.*"})
//@ImportResource("classpath:/META-INF/spring/dubbo-demo-provider.xml")
public class ServiceSetupApp {
    public static void main(String[] args) throws Exception {
//        SpringApplication.run(ServiceSetupApp.class, args);
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext
                (new String[]{"/META-INF/spring/dubbo-demo-provider.xml"});
        context.start();
        System.in.read();
    }
}

package com.abc.dubbo.demo;

import com.abc.dubbo.demo.api.bid.BidRequest;
import com.abc.dubbo.demo.api.bid.BidResponse;
import com.abc.dubbo.demo.api.bid.BidService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootApplication
//@ComponentScan(basePackages = {"com.abc.dubbo.*"})
//@ImportResource("classpath:/META-INF/spring/dubbo-demo-consumer.xml")
public class CustomerSetupApp {
    public static void main(String[] args) throws Exception {
        System.out.println("Start...");
        //D:/DevN/abc-end-workbench/core.framework.integration/src/test/resources/META-INF/spring
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext
                (new String[]{"/META-INF/spring/dubbo-demo-consumer.xml"});
        BidService bidService= (BidService) context.getBean("bidService");
        BidRequest request=new BidRequest();
        request.setId("001");
        BidResponse bidResponse=bidService.bid(request);
        System.out.println("bidResponse is->");
        System.out.println(bidResponse.getId());
        System.out.println("Over...");
    }
}

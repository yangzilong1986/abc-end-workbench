package com.abc.core.integraion.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Description: <web.xml通用设置>. <br>
 * <p>
 * <配置相关Listener，servlet，filter等等>
 * </p>
 */
@Order(1)
public class CommonInitializer implements WebApplicationInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("WebApplication Start");
        }
        //DemoServlet
        DispatcherServlet demoServlet = new DispatcherServlet();
        ServletRegistration.Dynamic dynamic = servletContext.addServlet(
                "demoServlet", demoServlet);
        dynamic.setLoadOnStartup(1);
        dynamic.addMapping("/services/*");

    }
}

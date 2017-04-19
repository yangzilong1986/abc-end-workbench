package com.abc.core.integraion.setup;

import javax.servlet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootConfiguration
@EnableAutoConfiguration
public class WebApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplication.class);

	@SuppressWarnings("serial")
	@Bean
	public Servlet dispatcherDubboServlet() {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("WebApplication Start");
		}
		DispatcherServlet dispatcherServlet = new DispatcherServlet();

		return dispatcherServlet;
	}

	@SuppressWarnings("serial")
	@Bean
	public Servlet dispatcherServlet() {
		return new GenericServlet() {
			@Override
			public void service(ServletRequest req, ServletResponse res)
					throws ServletException, IOException {
				res.setContentType("text/plain");
				res.getWriter().append("Hello World");
			}
		};
	}
	public void onStartupDispatcherServlet(ServletContext servletContext) throws ServletException {
		  if(LOGGER.isInfoEnabled()){
	            LOGGER.info("WebApplication Start");
	        }
	        //DemoServlet
	        DispatcherServlet demoServlet = new DispatcherServlet();
	        ServletRegistration.Dynamic dynamic = servletContext.addServlet(
	                "demoServlet", demoServlet);
	        dynamic.setLoadOnStartup(1);
	        dynamic.addMapping("/services/*");
	}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);

    }

	@Bean
	protected ServletContextListener listener() {
		return new ServletContextListener() {

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				LOGGER.info("ServletContext initialized");
				ServletContext servletContext=sce.getServletContext();
//				try {
//					onStartupDispatcherServlet(servletContext);
//				} catch (ServletException e) {
//					LOGGER.error("ServletContextError:",e.getRootCause());
//				}
			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				LOGGER.info("ServletContext destroyed");
			}

		};
	}
}
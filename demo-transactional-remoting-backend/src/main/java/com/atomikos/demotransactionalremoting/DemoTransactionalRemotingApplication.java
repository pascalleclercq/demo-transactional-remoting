package com.atomikos.demotransactionalremoting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.atomikos.demotransactionalremoting.api.AccountService;

@SpringBootApplication
public class DemoTransactionalRemotingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoTransactionalRemotingApplication.class, args);
	}
	
	@Autowired
	AccountService accountService;
	@Bean(name = "/account") 
	HttpInvokerServiceExporter accountServiceServiceExporter() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService( accountService );
        exporter.setServiceInterface( AccountService.class );
        return exporter;
    }
}

package com.atomikos.demotransactionalremoting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import account.AccountService;

import com.atomikos.http.spring.httpinvoker.AtomikosHttpPort;
import com.atomikos.http.spring.httpinvoker.TransactionalHttpInvokerServiceExporter;

@SpringBootApplication
public class DemoTransactionalRemotingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoTransactionalRemotingApplication.class, args);
	}
	
	@Autowired
	AccountService accountService;
	@Bean(name = "/account") 
	HttpInvokerServiceExporter accountServiceServiceExporter() {
        HttpInvokerServiceExporter exporter = new TransactionalHttpInvokerServiceExporter();
        exporter.setService( accountService );
        exporter.setServiceInterface( AccountService.class );
        return exporter;
    }
	
	@Bean(name = AtomikosHttpPort.ATOMIKOS_PORT_SERVICE_NAME) 
	HttpInvokerServiceExporter atomikosServiceServiceExporter() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService( new AtomikosTransactionPort() );
        exporter.setServiceInterface( AtomikosHttpPort.class );
        return exporter;
    }
}

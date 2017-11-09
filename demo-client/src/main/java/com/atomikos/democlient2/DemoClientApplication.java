package com.atomikos.democlient2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import payment.PaymentService;

import com.atomikos.http.spring.httpinvoker.TransactionalHttpInvokerRequestExecutor;

@SpringBootApplication
public class DemoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoClientApplication.class, args);
	}
	
	@Bean
	public PaymentService accountService() {
		HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
		invoker.setServiceUrl("http://localhost:8081/account");
		invoker.setServiceInterface(PaymentService.class);
		invoker.setHttpInvokerRequestExecutor(new TransactionalHttpInvokerRequestExecutor());
		invoker.afterPropertiesSet();
		return (PaymentService)invoker.getObject();
	}
	
	@Bean
	public CommandLineRunner demo(CustomerService customerService) {
		
		return new CommandLineRunner() {
			
			@Override
			public void run(String... args) throws Exception {
				customerService.createUserAndAccount();
				
			}
		}; 
		
			
	}
 	

}

package com.atomikos.demotransactionalremoting.client;

import java.util.Collection;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.atomikos.demotransactionalremoting.api.Account;
import com.atomikos.demotransactionalremoting.api.AccountService;

@Configuration
public class AccountClient {

	@Bean
	public HttpInvokerProxyFactoryBean invoker() {
		HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
		invoker.setServiceUrl("http://localhost:8081/account");
		invoker.setServiceInterface(AccountService.class);
		return invoker;
	}

	public static void main(String[] args) throws Exception {
		AccountService service = SpringApplication.run(AccountClient.class,
				args).getBean(AccountService.class);
		
		Collection<Account> accounts = service.listAccounts();
		System.out.println(accounts.size());
		
		Account newAccount = new Account();
		newAccount.setAccount(10);
		newAccount.setBalance(100f);
		newAccount.setOwner("account10");
		service.save(newAccount);
		
		accounts = service.listAccounts();
		System.out.println(accounts.size());
		
	}

}

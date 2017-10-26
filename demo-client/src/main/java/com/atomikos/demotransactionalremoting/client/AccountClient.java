package com.atomikos.demotransactionalremoting.client;

import java.util.Collection;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.atomikos.demotransactionalremoting.api.Account;
import com.atomikos.demotransactionalremoting.api.AccountService;
import com.atomikos.icatch.jta.UserTransactionManager;

@org.springframework.context.annotation.Configuration
public class AccountClient {

	@Bean
	public HttpInvokerProxyFactoryBean invoker() {
		HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
		invoker.setServiceUrl("http://localhost:8081/account");
		invoker.setServiceInterface(AccountService.class);
		invoker.setHttpInvokerRequestExecutor(new TransactionalHttpInvokerRequestExecutor());
		return invoker;
	}

	

	public static void main(String[] args) throws Exception {
		AccountService service = SpringApplication.run(AccountClient.class,
				args).getBean(AccountService.class);
		
		UserTransactionManager utm = new UserTransactionManager();
		utm.init();
		Collection<Account> accounts = service.listAccounts();
		System.out.println("There should be no account: "+accounts.size());
		createNewAccountTransactionnally(service, utm);
		
		accounts = service.listAccounts();
		
		System.out.println("There should be one account: "+accounts.size());
			
	}



	private static void createNewAccountTransactionnally(
			AccountService service, UserTransactionManager utm)
			throws NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException {
		utm.begin();		
		Account newAccount = new Account();
		newAccount.setAccount(10);
		newAccount.setBalance(100f);
		newAccount.setOwner("account10");
		service.save(newAccount);
		utm.commit();
	}

}

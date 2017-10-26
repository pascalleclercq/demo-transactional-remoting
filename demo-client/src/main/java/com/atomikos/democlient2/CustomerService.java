package com.atomikos.democlient2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomikos.demotransactionalremoting.api.Account;
import com.atomikos.demotransactionalremoting.api.AccountService;

@Service
@Transactional
public class CustomerService {

	@Autowired
	CustomerRepository repository;
	@Autowired
	AccountService accountService;
	
	public void createUserAndAccount() {
		repository.save(new Customer("Jack", "Bauer"));
		accountService.save(new Account("Jack Bauer", 50f)); //remote service
	}
}

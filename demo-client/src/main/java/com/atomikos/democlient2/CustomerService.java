package com.atomikos.democlient2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import payment.Payment;
import payment.PaymentService;

@Service
@Transactional
public class CustomerService {

	@Autowired
	CustomerRepository repository;
	@Autowired
	PaymentService accountService;
	
	public void createUserAndAccount() throws Exception {
		repository.save(new Customer("Jack", "Bauer"));
		accountService.pay(new Payment("Jack Bauer", 50)); //remote service
	}
}

package com.atomikos.demotransactionalremoting;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atomikos.demotransactionalremoting.api.Account;
import com.atomikos.demotransactionalremoting.api.AccountService;
@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepository accountRepository;
	
	public Account save(Account account) {
		return accountRepository.save(account);
	}
	
	public Account findByAccounId(int accountId){
		return accountRepository.findOne(accountId);
	}
	
	public void deleteAccount(int accountId){
		accountRepository.delete(accountId);
	}
	
	public Collection<Account> listAccounts() {
		return accountRepository.findAll();
	}
}

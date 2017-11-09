package com.atomikos.demotransactionalremoting;

import org.springframework.data.jpa.repository.JpaRepository;

import account.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}

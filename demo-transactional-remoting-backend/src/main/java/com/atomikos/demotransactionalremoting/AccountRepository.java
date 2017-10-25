package com.atomikos.demotransactionalremoting;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atomikos.demotransactionalremoting.api.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}

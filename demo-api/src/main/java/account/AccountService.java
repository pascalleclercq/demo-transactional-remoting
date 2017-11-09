package account;

import java.util.Collection;

public interface AccountService {

	Account save(Account account);

	Account findByAccounId(int accountId);

	void deleteAccount(int accountId);

	Collection<Account> listAccounts();

}
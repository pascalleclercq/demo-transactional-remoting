package inventory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import payment.Payment;
import payment.PaymentService;

@Service
@Transactional
public class InventoryService {

	@Autowired
	StockRepository repository;
	@Autowired
	PaymentService accountService;
	
	public void createUserAndAccount(Long itemId, Float qty, String cardno) throws Exception {
		repository.save(new Stock(itemId, qty));
		accountService.pay(new Payment(cardno, 50)); //remote service
	}
}

package inventory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	StockRepository repository;
	
	public void update(String itemId, int qty, String cardno) throws Exception {
		repository.save(new Stock(itemId, qty)); 
	}
}

package inventory;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface StockRepository extends CrudRepository<Stock, Long> {

}
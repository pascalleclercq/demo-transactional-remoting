package client;


import inventory.InventoryService;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import payment.Payment;
import payment.PaymentService;

import com.atomikos.http.spring.httpinvoker.TransactionalHttpInvokerRequestExecutor;
import com.atomikos.icatch.jta.UserTransactionManager;

public class EmbeddedTransactionManagerClient {
static String cardno = "card10";
	

	
	public static void main(String[] args) throws Exception {
		
		HttpInvokerProxyFactoryBean inventoryinvoker = new HttpInvokerProxyFactoryBean();
		inventoryinvoker.setServiceUrl("http://localhost:8082/inventory");
		inventoryinvoker.setServiceInterface(InventoryService.class);
		inventoryinvoker.setHttpInvokerRequestExecutor(new TransactionalHttpInvokerRequestExecutor());
		inventoryinvoker.afterPropertiesSet();
		InventoryService inventoryService =  (InventoryService)inventoryinvoker.getObject();

		
		
		HttpInvokerProxyFactoryBean paymentInvoker = new HttpInvokerProxyFactoryBean();
		paymentInvoker.setServiceUrl("http://localhost:8081/payment");
		paymentInvoker.setServiceInterface(PaymentService.class);
		paymentInvoker.setHttpInvokerRequestExecutor(new TransactionalHttpInvokerRequestExecutor());
		paymentInvoker.afterPropertiesSet();
		PaymentService paymentService =  (PaymentService)paymentInvoker.getObject();


		UserTransactionManager utm = new UserTransactionManager();
		utm.init();

		callBothInJtaTransaction(paymentService, inventoryService);
	}


	private static void callBothInJtaTransaction(PaymentService paymentService, InventoryService inventoryService) throws Exception {
		
		UserTransactionManager utm = new UserTransactionManager();
		utm.begin();
		pay(cardno, paymentService);
		
		updateInventory(cardno, inventoryService);
		utm.commit();
		
	}
	
	
	private static void pay(String cardno, PaymentService paymentService) throws Exception {
		Payment payment = new Payment();
		payment.amount =10;
		payment.cardno = cardno;
		Payment p =	paymentService.pay(payment);
		System.out.println(p);
	}
	
	

	private static void updateInventory(String cardno,
			InventoryService inventoryService) throws Exception {
		inventoryService.update("20", 2, cardno);
		
//		
		
	}
}

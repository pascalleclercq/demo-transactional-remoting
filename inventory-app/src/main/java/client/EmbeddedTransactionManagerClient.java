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
		InventoryService inventoryService = inventoryService();
		PaymentService paymentService = paymentService();
		UserTransactionManager utm = new UserTransactionManager();
		utm.init();

		callBothInJtaTransaction(paymentService, inventoryService);
	}


	private static PaymentService paymentService() {
		HttpInvokerProxyFactoryBean paymentInvoker = new HttpInvokerProxyFactoryBean();
		paymentInvoker.setServiceUrl("http://localhost:8081/payment");
		paymentInvoker.setServiceInterface(PaymentService.class);
		paymentInvoker.setHttpInvokerRequestExecutor(new TransactionalHttpInvokerRequestExecutor());
		paymentInvoker.afterPropertiesSet();
		PaymentService paymentService =  (PaymentService)paymentInvoker.getObject();
		return paymentService;
	}


	private static InventoryService inventoryService() {
		HttpInvokerProxyFactoryBean inventoryinvoker = new HttpInvokerProxyFactoryBean();
		inventoryinvoker.setServiceUrl("http://localhost:8082/inventory");
		inventoryinvoker.setServiceInterface(InventoryService.class);
		inventoryinvoker.setHttpInvokerRequestExecutor(new TransactionalHttpInvokerRequestExecutor());
		inventoryinvoker.afterPropertiesSet();
		InventoryService inventoryService =  (InventoryService)inventoryinvoker.getObject();
		return inventoryService;
	}


	private static void callBothInJtaTransaction(PaymentService paymentService, InventoryService inventoryService) throws Exception {
		
		UserTransactionManager utm = new UserTransactionManager();
		utm.begin();
		Payment payment = new Payment(cardno, 10);
		paymentService.pay(payment);
		inventoryService.update("20", 2);
		utm.commit();
		
	}
	
	
}

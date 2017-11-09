package payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import payment.PaymentService;

import com.atomikos.http.spring.httpinvoker.AtomikosHttpPort;
import com.atomikos.http.spring.httpinvoker.AtomikosTransactionPort;
import com.atomikos.http.spring.httpinvoker.TransactionalHttpInvokerServiceExporter;

@SpringBootApplication
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}
	
	@Autowired
	PaymentService paymentService;
	@Bean(name = "/payment") 
	HttpInvokerServiceExporter accountServiceServiceExporter() {
        HttpInvokerServiceExporter exporter = new TransactionalHttpInvokerServiceExporter();
        exporter.setService( paymentService );
        exporter.setServiceInterface( PaymentService.class );
        return exporter;
    }
	
	@Bean(name = AtomikosHttpPort.ATOMIKOS_PORT_SERVICE_NAME) 
	HttpInvokerServiceExporter atomikosServiceServiceExporter() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService( new AtomikosTransactionPort() );
        exporter.setServiceInterface( AtomikosHttpPort.class );
        return exporter;
    }
}

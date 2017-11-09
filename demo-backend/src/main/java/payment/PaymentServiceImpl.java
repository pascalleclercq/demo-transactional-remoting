package payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import payment.Payment;
import payment.PaymentService;
@Service
public class PaymentServiceImpl implements PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;

	@Override
	public Payment pay(Payment payment) throws Exception {
		
		return paymentRepository.save(payment);
	}
	
	
}

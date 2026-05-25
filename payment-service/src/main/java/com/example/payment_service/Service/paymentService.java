package com.example.payment_service.Service;

import com.example.payment_service.DTO.LogMessage;
import com.example.payment_service.DTO.PaymentRequest;
import com.example.payment_service.Entity.payment;
import com.example.payment_service.Repository.paymentRepository;
import com.example.payment_service.kafka.LogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class paymentService {
    private final paymentRepository paymentRepository;
    private final LogProducer logProducer;
    public  String processPayment(PaymentRequest paymentRequest){
        payment payment=new payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(payment.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);
        logProducer.sendlog(new LogMessage(
                "PAYMENT-SERVICE",
                "INFO",
                "Payment Successful"
        ));
        return "Payment Successful";
    }
}

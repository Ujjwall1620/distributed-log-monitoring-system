package com.example.payment_service.Service;

import com.example.payment_service.DTO.LogMessage;
import com.example.payment_service.DTO.PaymentRequest;
import com.example.payment_service.Entity.payment;
import com.example.payment_service.Repository.paymentRepository;
import com.example.payment_service.kafka.LogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class paymentService {
    private final paymentRepository paymentRepository;
    private final LogProducer logProducer;
    Random random = new Random();
    public  String processPayment(PaymentRequest paymentRequest){
        boolean condition=random.nextBoolean();
        System.out.println(condition);
        if (condition== true){
            payment payment=new payment();
            payment.setOrderId(paymentRequest.getOrderId());
            payment.setAmount(paymentRequest.getAmount());
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);
            logProducer.sendlog(new LogMessage(
                    "PAYMENT-SERVICE",
                    "INFO",
                    "Payment Successful",
                     LocalDateTime.now()
            ));
            return "Payment Successful";
        }
        payment payment=new payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(payment.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus("REJECTED");
        paymentRepository.save(payment);
        logProducer.sendlog(new LogMessage(
                "PAYMENT-REJECT",
                "INFO",
                "Payment Rejected",
                LocalDateTime.now()
        ));
        return "Payment Rejected!";

    }
}

package com.example.payment_service.Controller;

import com.example.payment_service.DTO.PaymentRequest;
import com.example.payment_service.Service.paymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class paymentController {

    private final paymentService paymentService;

    @PostMapping("/pay")
    public String pay(
            @RequestBody PaymentRequest request
    ){

        return paymentService
                .processPayment(request);
    }
}
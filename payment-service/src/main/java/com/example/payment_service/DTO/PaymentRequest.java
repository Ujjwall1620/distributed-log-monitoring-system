package com.example.payment_service.DTO;

import lombok.Data;

@Data
public class PaymentRequest {
    private  int OrderId;
    private Double amount;
    private String paymentMethod;
}

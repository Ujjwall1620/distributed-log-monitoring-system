package com.example.payment_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogMessage {

    private String serviceName;

    private String level;

    private String message;

    private LocalDateTime timestamp;
}
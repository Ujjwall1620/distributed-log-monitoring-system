package com.example.payment_service.kafka;

import com.example.payment_service.DTO.LogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogProducer {
    private final KafkaTemplate<String, LogMessage> kafkaTemplate;
    public void sendlog(LogMessage logMessage){
        kafkaTemplate.send("log-topic",logMessage);
    }
}

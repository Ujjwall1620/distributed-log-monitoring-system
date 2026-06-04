package com.example.log_processing_service.Kafka;

import com.example.log_processing_service.DTO.LogMessage;
import com.example.log_processing_service.Service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogConsumer {
    private final LogService logService;
    @KafkaListener(topics = "log-topic", groupId = "log-group")
    public void consumer(LogMessage logMessage){
        System.out.println(logMessage);
        logService.saveLog(logMessage);
    }
}

package com.example.log_processing_service.Service;


import com.example.log_processing_service.Entity.LogEntity;
import com.example.log_processing_service.Repository.LogRepository;
import com.example.payment_service.DTO.LogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {
    public final LogRepository logRepository;
    public void saveLog(LogMessage logMessage){
        LogEntity log = new LogEntity();
        log.setServiceName(logMessage.getServiceName());
        log.setMessage(logMessage.getMessage());
        log.setLevel(logMessage.getLevel());
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);

    }
}

package com.example.log_processing_service.Service;


import com.example.log_processing_service.DTO.LogMessage;
import com.example.log_processing_service.Entity.LogEntity;
import com.example.log_processing_service.Repository.LogRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogService {
    public final LogRepository logRepository;

    public void saveLog(LogMessage logMessage){
        LogEntity log = new LogEntity();
        log.setService(logMessage.getService());
        log.setMessage(logMessage.getMessage());
        log.setLevel(logMessage.getLevel());
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
        System.out.println(log);
    }

    public List<LogEntity> getLogs(String service,
                                   String level,
                                   String message,
                                   LocalDateTime startTime,
                                   LocalDateTime endTime){
        List<LogEntity> logs= logRepository.findAll();
        return logs.stream().filter(log -> service==null||log.getService().equalsIgnoreCase(service)).
                filter(log -> level==null||log.getLevel().equalsIgnoreCase(level))
                .filter(log->message==null||log.getMessage().equalsIgnoreCase(message))
                .filter(log -> startTime==null||!log.getTimestamp().isBefore(startTime))
                .filter(log -> endTime==null||log.getTimestamp().isBefore(endTime))
                .collect(Collectors.toList());
    }
}

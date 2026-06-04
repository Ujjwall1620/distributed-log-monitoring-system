package com.example.log_processing_service.Controller;

import com.example.log_processing_service.Entity.LogEntity;
import com.example.log_processing_service.Service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class logController {
    private final LogService logService;

    @GetMapping
    public List<LogEntity> GetLogs(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String message,

            @RequestParam(required = false)
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime EndTime
            ){
        return logService.getLogs(service,level,message,startTime,EndTime);
    }
}

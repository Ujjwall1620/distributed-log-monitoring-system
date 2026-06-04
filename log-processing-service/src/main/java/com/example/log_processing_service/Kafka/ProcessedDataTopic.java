package com.example.log_processing_service.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class ProcessedDataTopic {
    @Bean
    public NewTopic topic(){
        return TopicBuilder.name("processed-data").build();
    }
}

package com.example.log_processing_service.Repository;

import com.example.log_processing_service.Entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity,Long> {
}

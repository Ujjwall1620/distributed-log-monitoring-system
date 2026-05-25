package com.example.payment_service.Repository;

import com.example.payment_service.Entity.payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface paymentRepository extends JpaRepository<payment,Integer> {
}

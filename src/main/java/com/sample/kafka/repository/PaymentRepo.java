package com.sample.kafka.repository;

import com.sample.kafka.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    // You can add custom query methods here if needed
}

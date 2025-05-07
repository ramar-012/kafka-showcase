package com.sample.kafka.repository;

import com.sample.kafka.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    // You can add custom query methods here if needed
}

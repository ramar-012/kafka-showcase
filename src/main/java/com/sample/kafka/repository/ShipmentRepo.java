package com.sample.kafka.repository;

import com.sample.kafka.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
    // You can add custom query methods here if needed
}

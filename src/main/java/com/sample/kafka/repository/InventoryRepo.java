package com.sample.kafka.repository;

import com.sample.kafka.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
    // method to get maximum of order ID in inventory table
    Long findMaxOrderId();
}

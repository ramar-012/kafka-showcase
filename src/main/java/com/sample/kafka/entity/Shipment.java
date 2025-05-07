package com.sample.kafka.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "shipment")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(name = "status")
    private String shipmentStatus;

    @Column(name = "shipment_date")
    private Date shipmentDate;

    // Default constructor
    public Shipment() {
    }

    // Constructor with all fields
    public Shipment(Long id, Order order, String shipmentStatus, Date shipmentDate) {
        this.id = id;
        this.order = order;
        this.shipmentStatus = shipmentStatus;
        this.shipmentDate = shipmentDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }
}

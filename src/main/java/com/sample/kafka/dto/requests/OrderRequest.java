package com.sample.kafka.dto.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private String customerName;
    private BigDecimal totalAmount;
}

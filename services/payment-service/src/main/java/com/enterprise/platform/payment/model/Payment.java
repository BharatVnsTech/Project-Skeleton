package com.enterprise.platform.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private String id;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String status;

    public enum Status {
        SUCCESS, PENDING, FAILED
    }
}

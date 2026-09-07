package com.enterprise.platform.payment.repository;

import com.enterprise.platform.payment.model.Payment;
import com.enterprise.platform.payment.model.Payment.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {

    private static final List<Payment> PAYMENTS = List.of(
            Payment.builder().id("PAY-1001").userId(101L).amount(new BigDecimal("500.00")).currency("INR").status(Status.SUCCESS.name()).build(),
            Payment.builder().id("PAY-1002").userId(102L).amount(new BigDecimal("1200.50")).currency("INR").status(Status.SUCCESS.name()).build(),
            Payment.builder().id("PAY-1003").userId(103L).amount(new BigDecimal("350.00")).currency("INR").status(Status.PENDING.name()).build(),
            Payment.builder().id("PAY-1004").userId(101L).amount(new BigDecimal("750.00")).currency("INR").status(Status.FAILED.name()).build(),
            Payment.builder().id("PAY-1005").userId(104L).amount(new BigDecimal("2000.00")).currency("INR").status(Status.SUCCESS.name()).build()
    );

    public List<Payment> findAll() {
        return PAYMENTS;
    }

    public Optional<Payment> findById(String id) {
        return PAYMENTS.stream()
                .filter(payment -> payment.getId().equals(id))
                .findFirst();
    }
}

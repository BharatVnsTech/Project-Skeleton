package com.enterprise.platform.payment.service;

import com.enterprise.platform.exception.ErrorCode;
import com.enterprise.platform.exception.ResourceNotFoundException;
import com.enterprise.platform.payment.model.Payment;
import com.enterprise.platform.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = paymentRepository.findAll();
        log.info("Found {} payments", payments.size());
        return payments;
    }

    public Payment getPaymentById(String id) {
        log.info("Fetching payment with id: {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND,
                            "Payment not found with id: " + id);
                });
    }
}

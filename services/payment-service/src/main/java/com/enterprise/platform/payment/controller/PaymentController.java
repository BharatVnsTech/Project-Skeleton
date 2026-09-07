package com.enterprise.platform.payment.controller;

import com.enterprise.platform.core.CorrelationConstants;
import com.enterprise.platform.logging.CorrelationIdContext;
import com.enterprise.platform.payment.model.Payment;
import com.enterprise.platform.payment.service.PaymentService;
import com.enterprise.platform.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        log.info("GET /api/v1/payments - correlationId={}", correlationId);

        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully", correlationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable String id, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        log.info("GET /api/v1/payments/{} - correlationId={}", id, correlationId);

        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment retrieved successfully", correlationId));
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CorrelationConstants.CORRELATION_ID_HEADER);
        return CorrelationIdContext.getOrCreate(correlationId);
    }
}

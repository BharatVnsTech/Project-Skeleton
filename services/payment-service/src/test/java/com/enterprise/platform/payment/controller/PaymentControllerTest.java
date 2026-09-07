package com.enterprise.platform.payment.controller;

import com.enterprise.platform.payment.model.Payment;
import com.enterprise.platform.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void getAllPayments_shouldReturnPayments() throws Exception {
        Payment payment = Payment.builder()
                .id("PAY-1001").userId(101L)
                .amount(new BigDecimal("500.00"))
                .currency("INR").status("SUCCESS").build();

        when(paymentService.getAllPayments()).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("PAY-1001"))
                .andExpect(jsonPath("$.data[0].status").value("SUCCESS"));
    }

    @Test
    void getPaymentById_shouldReturnPayment() throws Exception {
        Payment payment = Payment.builder()
                .id("PAY-1001").userId(101L)
                .amount(new BigDecimal("500.00"))
                .currency("INR").status("SUCCESS").build();

        when(paymentService.getPaymentById("PAY-1001")).thenReturn(payment);

        mockMvc.perform(get("/api/v1/payments/PAY-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("PAY-1001"))
                .andExpect(jsonPath("$.data.amount").value(500.00));
    }
}

package br.com.tiago.payment.service;

import br.com.tiago.payment.dto.PaymentResultDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentMethodService {
    PaymentResultDTO processPayment(Long paymentId, Long orderId, BigDecimal amount, Map<String, Object> paymentDetails);
}

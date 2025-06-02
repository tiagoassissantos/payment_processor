package br.com.tiago.payment.service;

import br.com.tiago.payment.dto.PaymentResultDTO;
import br.com.tiago.payment.entity.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class SimulatedPaymentService implements PaymentMethodService {

    @Override
    public PaymentResultDTO processPayment(Long paymentId, Long orderId, BigDecimal amount, Map<String, Object> paymentDetails) {
        // Simulate a payment processing delay
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        boolean success = (paymentId != null && paymentId % 2 == 1); // true if odd
        String status = success ? PaymentStatus.PAID.name() : PaymentStatus.FAILED.name();
        String gatewayResponse = success ? "Payment approved by gateway" : "Payment declined by gateway";
        LocalDateTime now = LocalDateTime.now();
        return new PaymentResultDTO(
                paymentId,
                orderId,
                status,
                gatewayResponse,
                now,
                now
        );
    }
}

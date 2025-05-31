package br.com.tiago.payment.service;

import br.com.tiago.payment.dto.OrderCreatedEventDTO;
import br.com.tiago.payment.rest.OrderRestClient;
import br.com.tiago.payment.entity.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class PaymentService {
    @Inject
    OrderRestClient orderRestClient;

    @Transactional
    public void processPayment(OrderCreatedEventDTO orderDTO) {
        boolean paymentSuccess = simulatePaymentGateway(orderDTO);

        Payment payment = new Payment();
        payment.orderId = orderDTO.id();
        payment.status = paymentSuccess ? "PAID" : "FAILED";
        payment.processedAt = LocalDateTime.now();
        payment.persist();

        if (paymentSuccess) {
            orderRestClient.updateOrderStatus(orderDTO.id(), "PAID");
        }
    }

    private boolean simulatePaymentGateway(OrderCreatedEventDTO orderDTO) {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return true;
    }
}


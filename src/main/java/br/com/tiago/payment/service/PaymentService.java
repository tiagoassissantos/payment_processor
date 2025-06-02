package br.com.tiago.payment.service;

import br.com.tiago.payment.dto.OrderCreatedEventDTO;
import br.com.tiago.payment.dto.PaymentResultDTO;
import br.com.tiago.payment.rest.OrderRestClient;
import br.com.tiago.payment.entity.Payment;
import br.com.tiago.payment.entity.PaymentResult;
import br.com.tiago.payment.entity.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;

@ApplicationScoped
public class PaymentService {
    @Inject
    @RestClient
    private OrderRestClient orderRestClient;

    @Inject
    private PaymentMethodService simulatedPaymentService;

    @Transactional
    public void processPayment(OrderCreatedEventDTO orderDTO) {
        Payment payment = findOrCreatePayment(orderDTO.id());
        PaymentResultDTO paymentResultDTO = simulatedPaymentService
                .processPayment(payment.id, orderDTO.id(), orderDTO.totalValue(), null);
        createAndPersistPaymentResult(paymentResultDTO);

        payment.status = paymentResultDTO.status();
        payment.persist();

        orderRestClient.updateOrderStatus(orderDTO.id(), payment.status);
    }

    protected Payment findOrCreatePayment(Long orderId) {
        Payment payment = Payment.<Payment>find("orderId", orderId)
                .firstResultOptional()
                .orElseGet(Payment::new);

        payment.orderId = orderId;
        payment.status = PaymentStatus.PROCESSING.name();
        payment.processedAt = LocalDateTime.now();
        payment.persist();
        return payment;
    }

    protected void createAndPersistPaymentResult(PaymentResultDTO paymentResultDTO) {
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.paymentId = paymentResultDTO.paymentId();
        paymentResult.orderId = paymentResultDTO.orderId();
        paymentResult.status = paymentResultDTO.status();
        paymentResult.gatewayResponse = paymentResultDTO.gatewayResponse();
        paymentResult.createdAt = paymentResultDTO.createdAt();
        paymentResult.updatedAt = paymentResultDTO.updatedAt();
        paymentResult.persist();
    }
}

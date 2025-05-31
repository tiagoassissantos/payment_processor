package br.com.tiago.payment.messaging;

import br.com.tiago.payment.dto.OrderCreatedEventDTO;
import br.com.tiago.payment.service.PaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class PaymentConsumer {
    @Inject
    PaymentService paymentService;

    @Incoming("order-created")
    public void consumeOrderCreated(OrderCreatedEventDTO orderDTO) {
        paymentService.processPayment(orderDTO);
    }
}


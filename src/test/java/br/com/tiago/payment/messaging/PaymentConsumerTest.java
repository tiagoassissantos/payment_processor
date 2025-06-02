package br.com.tiago.payment.messaging;

import br.com.tiago.payment.dto.OrderCreatedEventDTO;
import br.com.tiago.payment.service.PaymentService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.inject.Inject;
import java.math.BigDecimal;

@QuarkusTest
public class PaymentConsumerTest {

    @Inject
    PaymentConsumer paymentConsumer;

    @InjectMock
    PaymentService paymentService;

    @Inject @Any
    InMemoryConnector connector;

    @BeforeEach
    public void setup() {
        InMemoryConnector.clear();
        InMemoryConnector.switchIncomingChannelsToInMemory("order-created");
    }

    @Test
    void testConsumeOrderCreated() throws Exception {
        // Arrange
        OrderCreatedEventDTO orderDTO = new OrderCreatedEventDTO(1L, new BigDecimal("100.00"));

        // Act - Send message to the channel
        InMemorySource<OrderCreatedEventDTO> source = connector.source("order-created");
        source.send(orderDTO);

        // Assert - Give some time for async processing
        Thread.sleep(1000);

        Mockito.verify(paymentService).processPayment(orderDTO);
    }
}

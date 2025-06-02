package br.com.tiago.payment.service;

import br.com.tiago.payment.dto.OrderCreatedEventDTO;
import br.com.tiago.payment.dto.PaymentResultDTO;
import br.com.tiago.payment.entity.Payment;
import br.com.tiago.payment.entity.PaymentStatus;
import br.com.tiago.payment.rest.OrderRestClient;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class PaymentServiceTest {
    @Inject
    private PaymentService paymentService;
    @InjectMock
    private PaymentMethodService simulatedPaymentService;
    @InjectMock
    @RestClient
    private OrderRestClient orderRestClient;

    @Test
    void testProcessPayment_successfulPayment() {
        // Arrange: create and persist a Payment, or let your code create it
        OrderCreatedEventDTO orderDTO = new OrderCreatedEventDTO(1L, new BigDecimal("300.00"));

        PaymentResultDTO successfulResultDTO = new PaymentResultDTO(
                10L,
                orderDTO.id(),
                PaymentStatus.PAID.name(),
                "Payment successful",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(simulatedPaymentService.processPayment(anyLong(), anyLong(), any(), any())).thenReturn(successfulResultDTO);
        doNothing().when(orderRestClient).updateOrderStatus(eq(1L), eq(PaymentStatus.PAID.name()));

        // Act
        paymentService.processPayment(orderDTO);

        // Assert: fetch from DB to verify state after persistence
        Payment payment = Payment.find("orderId", orderDTO.id()).firstResult();
        assertNotNull(payment);
        assertEquals(orderDTO.id(), payment.orderId);
        assertEquals(PaymentStatus.PAID.name(), payment.status);
    }

    @Test
    void testProcessPayment_failedPayment() {
        // Arrange
        OrderCreatedEventDTO orderDTO = new OrderCreatedEventDTO(2L, new BigDecimal("100.00"));

        PaymentResultDTO failedResultDTO = new PaymentResultDTO(
                20L,
                orderDTO.id(),
                PaymentStatus.FAILED.name(),
                "Payment failed",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(simulatedPaymentService.processPayment(anyLong(), anyLong(), any(), any())).thenReturn(failedResultDTO);

        // Act
        paymentService.processPayment(orderDTO);

        // Assert
        Payment payment = Payment.find("orderId", orderDTO.id()).firstResult();
        assertNotNull(payment);
        assertEquals(orderDTO.id(), payment.orderId);
        assertEquals(PaymentStatus.FAILED.name(), payment.status);
        verify(orderRestClient).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void testProcessPayment_ThrowsWhenFindPayment() {
        // Arrange
        OrderCreatedEventDTO orderDTO = new OrderCreatedEventDTO(3L, new BigDecimal("200.00"));
        PaymentService spiedService = spy(paymentService);
        doThrow(new RuntimeException("DB error")).when(spiedService).findOrCreatePayment(anyLong());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> spiedService.processPayment(orderDTO));
        verify(orderRestClient, never()).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void testProcessPayment_paymentPersistThrows() {
        // Arrange
        OrderCreatedEventDTO orderDTO = new OrderCreatedEventDTO(5L, new BigDecimal("500.00"));
        PaymentService spiedService = spy(paymentService);
        PanacheMock.mock(Payment.class);
        Payment payment = mock(Payment.class);
        PaymentResultDTO resultDTO = new PaymentResultDTO(
                50L, orderDTO.id(), PaymentStatus.PAID.name(), "ok", LocalDateTime.now(), LocalDateTime.now()
        );
        doReturn(payment).when(spiedService).findOrCreatePayment(anyLong());
        doReturn(resultDTO).when(simulatedPaymentService).processPayment(anyLong(), anyLong(), any(), any());
        doThrow(new RuntimeException("DB error")).when(payment).persist();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> spiedService.processPayment(orderDTO));
        verify(orderRestClient, never()).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void testProcessPayment_orderRestClientThrows() {
        // Arrange
        OrderCreatedEventDTO orderDTO = new OrderCreatedEventDTO(6L, new BigDecimal("600.00"));
        PaymentService spiedService = spy(paymentService);
        Payment payment = new Payment();
        payment.id = 60L;
        payment.orderId = orderDTO.id();
        payment.status = PaymentStatus.PROCESSING.name();
        PaymentResultDTO resultDTO = new PaymentResultDTO(
                60L, orderDTO.id(), PaymentStatus.PAID.name(), "ok", LocalDateTime.now(), LocalDateTime.now()
        );
        doReturn(payment).when(spiedService).findOrCreatePayment(anyLong());
        doReturn(resultDTO).when(simulatedPaymentService).processPayment(anyLong(), anyLong(), any(), any());
        doNothing().when(spiedService).createAndPersistPaymentResult(any());
        doThrow(new RuntimeException("Order service failed")).when(orderRestClient).updateOrderStatus(anyLong(), anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> spiedService.processPayment(orderDTO));
        verify(orderRestClient).updateOrderStatus(anyLong(), anyString());
    }
}


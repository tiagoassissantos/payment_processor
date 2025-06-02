package br.com.tiago.payment.dto;

import java.time.LocalDateTime;

public record PaymentResultDTO(
    Long paymentId,
    Long orderId,
    String status,
    String gatewayResponse,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}


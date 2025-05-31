package br.com.tiago.payment.dto;

import java.math.BigDecimal;

public record OrderCreatedEventDTO(Long id, BigDecimal totalValue) {}

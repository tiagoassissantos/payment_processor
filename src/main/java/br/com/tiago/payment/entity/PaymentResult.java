package br.com.tiago.payment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_results")
public class PaymentResult extends PanacheEntity {
    public Long paymentId;
    public Long orderId;
    public String status;
    @Column(length = 1000)
    public String gatewayResponse;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}


package org.example.pms.orders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "source_platform", nullable = false)
    private String sourcePlatform;
    @Column(name = "min_target_amount", nullable = false)
    private BigDecimal minTargetAmount;
    @Column(name = "current_total", nullable = false)
    private BigDecimal currentTotal;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(nullable = false)
    private Instant deadline;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Version
    private Long version;
}

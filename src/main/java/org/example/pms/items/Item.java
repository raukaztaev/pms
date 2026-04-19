package org.example.pms.items;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    private UUID id;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "text")
    private String link;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    @Column(columnDefinition = "text")
    private String notes;
    @Column(nullable = false)
    private boolean distributed;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

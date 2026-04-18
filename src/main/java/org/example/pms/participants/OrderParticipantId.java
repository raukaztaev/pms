package org.example.pms.participants;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OrderParticipantId implements Serializable {
    private UUID orderId;
    private UUID userId;

    public OrderParticipantId() {}
    public OrderParticipantId(UUID orderId, UUID userId) {
        this.orderId = orderId;
        this.userId = userId;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getUserId() { return userId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderParticipantId that = (OrderParticipantId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, userId);
    }
}

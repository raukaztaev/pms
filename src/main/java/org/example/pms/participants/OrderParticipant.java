package org.example.pms.participants;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "order_participants")
public class OrderParticipant {
    @EmbeddedId
    private OrderParticipantId id;
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;
}

package org.example.pms.notifications;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String type;
    @Column(columnDefinition = "text", nullable = false)
    private String payload;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;
    @Column(name = "delivered_at")
    private Instant deliveredAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

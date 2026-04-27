package org.example.pms.notifications;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void create(UUID userId, String type, String payload) {
        Notification n = new Notification();
        n.setId(UUID.randomUUID());
        n.setUserId(userId);
        n.setType(type);
        n.setPayload(payload);
        n.setChannel(NotificationChannel.IN_APP);
        n.setCreatedAt(Instant.now());
        notificationRepository.save(n);
    }

    public List<NotificationResponse> my(UUID userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getType(), n.getPayload(), n.getChannel().name(), n.getDeliveredAt(), n.getCreatedAt()))
                .toList();
    }
}

package org.example.pms.participants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderParticipantRepository extends JpaRepository<OrderParticipant, OrderParticipantId> {
    boolean existsByIdOrderIdAndIdUserId(UUID orderId, UUID userId);
    List<OrderParticipant> findAllByIdOrderId(UUID orderId);
}

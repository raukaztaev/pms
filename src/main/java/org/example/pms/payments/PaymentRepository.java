package org.example.pms.payments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByOrderId(UUID orderId);
    Optional<Payment> findByOrderIdAndUserId(UUID orderId, UUID userId);
}

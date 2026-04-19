package org.example.pms.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> findAllByOrderId(UUID orderId);
    List<Item> findAllByOrderIdAndUserId(UUID orderId, UUID userId);

    @Query("select coalesce(sum(i.unitPrice * i.quantity), 0) from Item i where i.orderId = :orderId")
    BigDecimal sumOrderTotal(UUID orderId);
}

package org.example.pms.items;

import org.example.pms.common.exception.ApiException;
import org.example.pms.orders.Order;
import org.example.pms.orders.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    public ItemService(ItemRepository itemRepository, OrderService orderService) {
        this.itemRepository = itemRepository;
        this.orderService = orderService;
    }

    @Transactional
    public ItemResponse markDistributed(UUID itemId, UUID userId, String role) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));
        Order order = orderService.getRequired(item.getOrderId());
        if (!role.equals("ADMIN") && !order.getOrganizerId().equals(userId)) throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed");
        item.setDistributed(true);
        itemRepository.save(item);
        return new ItemResponse(item.getId(), item.getOrderId(), item.getUserId(), item.getName(), item.getLink(), item.getQuantity(), item.getUnitPrice(), item.getNotes(), true, item.getCreatedAt());
    }
}

package org.example.pms.orders;

import jakarta.validation.Valid;
import org.example.pms.common.security.CurrentUser;
import org.example.pms.items.AddItemRequest;
import org.example.pms.items.ItemResponse;
import org.example.pms.payments.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final CurrentUser currentUser;

    public OrderController(OrderService orderService, CurrentUser currentUser) {
        this.orderService = orderService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(currentUser.id(), request);
    }

    @GetMapping
    public Page<OrderResponse> list(@RequestParam(required = false) OrderStatus status, Pageable pageable) {
        return orderService.list(status, pageable);
    }

    @GetMapping("/{id}")
    public OrderDetailsResponse details(@PathVariable UUID id) {
        return orderService.details(id);
    }

    @PostMapping("/{id}/join")
    public OrderDetailsResponse join(@PathVariable UUID id) {
        return orderService.join(id, currentUser.id());
    }

    @PostMapping("/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse addItem(@PathVariable UUID id, @Valid @RequestBody AddItemRequest request) {
        return orderService.addItem(id, currentUser.id(), request.name(), request.link(), request.quantity(), request.unitPrice(), request.notes());
    }

    @GetMapping("/{id}/items")
    public List<ItemResponse> items(@PathVariable UUID id) {
        return orderService.items(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        return orderService.changeStatus(id, currentUser.id(), currentUser.role(), request.status());
    }

    @GetMapping("/{id}/payments")
    public List<PaymentResponse> payments(@PathVariable UUID id) {
        return orderService.payments(id);
    }
}

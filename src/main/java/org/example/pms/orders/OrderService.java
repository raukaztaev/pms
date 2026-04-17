package org.example.pms.orders;

import org.example.pms.common.exception.ApiException;
import org.example.pms.items.Item;
import org.example.pms.items.ItemRepository;
import org.example.pms.items.ItemResponse;
import org.example.pms.notifications.NotificationService;
import org.example.pms.participants.OrderParticipant;
import org.example.pms.participants.OrderParticipantId;
import org.example.pms.participants.OrderParticipantRepository;
import org.example.pms.payments.Payment;
import org.example.pms.payments.PaymentRepository;
import org.example.pms.payments.PaymentResponse;
import org.example.pms.payments.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderParticipantRepository participantRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final ItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository, OrderParticipantRepository participantRepository, OrderStatusHistoryRepository historyRepository, ItemRepository itemRepository, PaymentRepository paymentRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.participantRepository = participantRepository;
        this.historyRepository = historyRepository;
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    public Order getRequired(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional
    public OrderResponse create(UUID userId, CreateOrderRequest request) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrganizerId(userId);
        order.setProductName(request.productName());
        order.setSourcePlatform(request.sourcePlatform());
        order.setMinTargetAmount(request.minTargetAmount());
        order.setCurrentTotal(BigDecimal.ZERO);
        order.setStatus(OrderStatus.OPEN);
        order.setDeadline(request.deadline());
        order.setCreatedAt(Instant.now());
        orderRepository.save(order);
        OrderParticipant participant = new OrderParticipant();
        participant.setId(new OrderParticipantId(order.getId(), userId));
        participant.setJoinedAt(Instant.now());
        participantRepository.save(participant);
        addHistory(order.getId(), null, OrderStatus.OPEN, userId);
        return map(order);
    }

    public Page<OrderResponse> list(OrderStatus status, Pageable pageable) {
        return (status == null ? orderRepository.findAll(pageable) : orderRepository.findAllByStatus(status, pageable)).map(this::map);
    }

    public OrderDetailsResponse details(UUID id) {
        Order order = getRequired(id);
        List<UUID> participants = participantRepository.findAllByIdOrderId(id).stream().map(p -> p.getId().getUserId()).toList();
        List<ItemResponse> items = itemRepository.findAllByOrderId(id).stream().map(this::mapItem).toList();
        List<PaymentResponse> payments = paymentRepository.findAllByOrderId(id).stream().map(this::mapPayment).toList();
        return new OrderDetailsResponse(map(order), participants, items, payments);
    }

    @Transactional
    public OrderDetailsResponse join(UUID orderId, UUID userId) {
        Order order = getRequired(orderId);
        if (order.getStatus() != OrderStatus.OPEN) throw new ApiException(HttpStatus.CONFLICT, "Order is not OPEN");
        if (order.getDeadline().isBefore(Instant.now())) throw new ApiException(HttpStatus.CONFLICT, "Deadline has passed");
        if (participantRepository.existsByIdOrderIdAndIdUserId(orderId, userId)) throw new ApiException(HttpStatus.CONFLICT, "User already joined this order");
        OrderParticipant p = new OrderParticipant();
        p.setId(new OrderParticipantId(orderId, userId));
        p.setJoinedAt(Instant.now());
        participantRepository.save(p);
        notificationService.create(order.getOrganizerId(), "ORDER_JOINED", "{\"orderId\":\"" + orderId + "\",\"userId\":\"" + userId + "\"}");
        return details(orderId);
    }

    @Transactional
    public ItemResponse addItem(UUID orderId, UUID userId, String name, String link, Integer quantity, BigDecimal unitPrice, String notes) {
        Order order = getRequired(orderId);
        if (!participantRepository.existsByIdOrderIdAndIdUserId(orderId, userId)) throw new ApiException(HttpStatus.FORBIDDEN, "User is not a participant");
        if (order.getStatus() != OrderStatus.OPEN) throw new ApiException(HttpStatus.CONFLICT, "Order is not OPEN");
        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setOrderId(orderId);
        item.setUserId(userId);
        item.setName(name);
        item.setLink(link);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setNotes(notes);
        item.setDistributed(false);
        item.setCreatedAt(Instant.now());
        itemRepository.save(item);
        recalcTotals(orderId, userId);
        return mapItem(item);
    }

    @Transactional
    public OrderResponse changeStatus(UUID orderId, UUID byUserId, String role, OrderStatus toStatus) {
        Order order = getRequired(orderId);
        if (!role.equals("ADMIN") && !order.getOrganizerId().equals(byUserId)) throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed");
        validateTransition(order.getStatus(), toStatus);
        OrderStatus from = order.getStatus();
        order.setStatus(toStatus);
        orderRepository.save(order);
        addHistory(orderId, from, toStatus, byUserId);
        for (UUID participantId : participantRepository.findAllByIdOrderId(orderId).stream().map(x -> x.getId().getUserId()).toList()) {
            notificationService.create(participantId, "ORDER_STATUS_CHANGED", "{\"orderId\":\"" + orderId + "\",\"from\":\"" + from + "\",\"to\":\"" + toStatus + "\"}");
        }
        return map(order);
    }

    private void validateTransition(OrderStatus from, OrderStatus to) {
        if (from == to) return;
        if (to == OrderStatus.CANCELLED && from != OrderStatus.DISTRIBUTED) return;
        Set<String> allowed = Set.of("OPEN:FUNDED", "FUNDED:ORDERED", "ORDERED:SHIPPED", "SHIPPED:ARRIVED", "ARRIVED:DISTRIBUTED");
        if (!allowed.contains(from.name() + ":" + to.name())) throw new ApiException(HttpStatus.CONFLICT, "Invalid status transition");
    }

    private void addHistory(UUID orderId, OrderStatus from, OrderStatus to, UUID by) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setId(UUID.randomUUID());
        h.setOrderId(orderId);
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setChangedBy(by);
        h.setChangedAt(Instant.now());
        historyRepository.save(h);
    }

    @Transactional
    public void recalcTotals(UUID orderId, UUID userId) {
        Order order = getRequired(orderId);
        BigDecimal total = itemRepository.sumOrderTotal(orderId);
        order.setCurrentTotal(total);
        orderRepository.save(order);
        BigDecimal userTotal = itemRepository.findAllByOrderIdAndUserId(orderId, userId).stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Payment p = paymentRepository.findByOrderIdAndUserId(orderId, userId).orElseGet(Payment::new);
        if (p.getId() == null) {
            p.setId(UUID.randomUUID());
            p.setOrderId(orderId);
            p.setUserId(userId);
            p.setStatus(PaymentStatus.UNPAID);
            p.setCreatedAt(Instant.now());
        }
        p.setAmount(userTotal);
        paymentRepository.save(p);
    }

    public List<PaymentResponse> payments(UUID orderId) {
        getRequired(orderId);
        return paymentRepository.findAllByOrderId(orderId).stream().map(this::mapPayment).toList();
    }

    public List<ItemResponse> items(UUID orderId) {
        getRequired(orderId);
        return itemRepository.findAllByOrderId(orderId).stream().map(this::mapItem).toList();
    }

    private OrderResponse map(Order o) {
        return new OrderResponse(o.getId(), o.getOrganizerId(), o.getProductName(), o.getSourcePlatform(), o.getMinTargetAmount(), o.getCurrentTotal(), o.getStatus().name(), o.getDeadline(), o.getCreatedAt());
    }

    private ItemResponse mapItem(Item i) {
        return new ItemResponse(i.getId(), i.getOrderId(), i.getUserId(), i.getName(), i.getLink(), i.getQuantity(), i.getUnitPrice(), i.getNotes(), i.isDistributed(), i.getCreatedAt());
    }

    private PaymentResponse mapPayment(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getUserId(), p.getAmount(), p.getStatus().name(), p.getCreatedAt());
    }
}

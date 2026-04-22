package org.example.pms.payments;

import org.example.pms.common.exception.ApiException;
import org.example.pms.orders.Order;
import org.example.pms.orders.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Transactional
    public PaymentResponse changeStatus(UUID paymentId, UUID userId, String role, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found"));
        Order order = orderService.getRequired(payment.getOrderId());
        if (!role.equals("ADMIN") && !order.getOrganizerId().equals(userId)) throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed");
        payment.setStatus(status);
        paymentRepository.save(payment);
        return new PaymentResponse(payment.getId(), payment.getOrderId(), payment.getUserId(), payment.getAmount(), payment.getStatus().name(), payment.getCreatedAt());
    }
}

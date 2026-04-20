package org.example.pms.orders;

import org.example.pms.items.ItemResponse;
import org.example.pms.payments.PaymentResponse;

import java.util.List;
import java.util.UUID;

public record OrderDetailsResponse(OrderResponse order, List<UUID> participants, List<ItemResponse> items, List<PaymentResponse> payments) {}

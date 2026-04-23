package org.example.pms.payments;

import jakarta.validation.Valid;
import org.example.pms.common.security.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final CurrentUser currentUser;

    public PaymentController(PaymentService paymentService, CurrentUser currentUser) {
        this.paymentService = paymentService;
        this.currentUser = currentUser;
    }

    @PatchMapping("/{id}/status")
    public PaymentResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return paymentService.changeStatus(id, currentUser.id(), currentUser.role(), request.status());
    }
}

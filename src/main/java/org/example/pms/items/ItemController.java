package org.example.pms.items;

import org.example.pms.common.security.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService itemService;
    private final CurrentUser currentUser;

    public ItemController(ItemService itemService, CurrentUser currentUser) {
        this.itemService = itemService;
        this.currentUser = currentUser;
    }

    @PatchMapping("/{id}/distributed")
    public ItemResponse markDistributed(@PathVariable UUID id) {
        return itemService.markDistributed(id, currentUser.id(), currentUser.role());
    }
}

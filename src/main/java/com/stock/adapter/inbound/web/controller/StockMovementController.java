package com.stock.adapter.inbound.web.controller;

import com.stock.application.dto.command.RecordStockMovementCommand;
import com.stock.application.dto.response.StockMovementDto;
import com.stock.application.usecase.ListStockMovementsUseCase;
import com.stock.application.usecase.RecordStockMovementUseCase;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {
    private final RecordStockMovementUseCase recordMovement;
    private final ListStockMovementsUseCase listMovements;

    public StockMovementController(RecordStockMovementUseCase recordMovement, ListStockMovementsUseCase listMovements) {
        this.recordMovement = recordMovement;
        this.listMovements = listMovements;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STOCK_MANAGER', 'STOCK_ADMIN')")
    public ResponseEntity<StockMovementDto> create(@Valid @RequestBody RecordStockMovementCommand command,
            Authentication authentication) {
        return ResponseEntity.ok(recordMovement.execute(command, authorName(authentication)));
    }

    private String authorName(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            String username = jwtAuthentication.getToken().getClaimAsString("preferred_username");
            if (username != null && !username.isBlank()) {
                return username;
            }
        }
        return authentication.getName() == null || authentication.getName().isBlank()
                ? "authenticated-user" : authentication.getName();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STOCK_VIEWER', 'STOCK_MANAGER', 'STOCK_ADMIN')")
    public ResponseEntity<java.util.List<StockMovementDto>> list(@RequestParam UUID productId) {
        return ResponseEntity.ok(listMovements.execute(productId));
    }
}

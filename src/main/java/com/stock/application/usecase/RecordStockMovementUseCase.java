package com.stock.application.usecase;

import com.stock.application.dto.command.RecordStockMovementCommand;
import com.stock.application.dto.response.StockMovementDto;
import com.stock.core.domain.entity.Product;
import com.stock.core.domain.entity.StockMovement;
import com.stock.core.domain.exception.EntityNotFoundException;
import com.stock.core.domain.port.ClockPort;
import com.stock.core.domain.port.ProductRepositoryPort;
import com.stock.core.domain.port.StockMovementRepositoryPort;
import com.stock.core.domain.valueobject.Quantity;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordStockMovementUseCase {
    private final ProductRepositoryPort products;
    private final StockMovementRepositoryPort movements;
    private final ClockPort clock;

    public RecordStockMovementUseCase(ProductRepositoryPort products, StockMovementRepositoryPort movements,
            ClockPort clock) {
        this.products = products;
        this.movements = movements;
        this.clock = clock;
    }

    @Transactional
    public StockMovementDto execute(RecordStockMovementCommand command, String authorUsername) {
        Product product = products.findById(command.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product", command.productId()));
        Quantity quantity = new Quantity(command.quantity());
        product.applyStockMovement(command.type(), quantity);
        Instant now = clock.now();
        StockMovement movement = new StockMovement(null, product.id(), command.type(), quantity, command.reason(),
                authorUsername, now, now);
        StockMovement saved = movements.save(movement);
        products.save(product);
        return new StockMovementDto(saved.id(), saved.productId(), saved.type(), saved.quantity().value(),
                saved.reason(), saved.authorUsername(), saved.movementDate());
    }
}

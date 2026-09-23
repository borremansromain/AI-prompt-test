package com.stock.application.usecase;

import com.stock.application.dto.command.CreateProductCommand;
import com.stock.application.dto.response.ProductDto;
import com.stock.core.domain.entity.Product;
import com.stock.core.domain.exception.EntityNotFoundException;
import com.stock.core.domain.exception.DuplicateSkuException;
import com.stock.core.domain.port.CategoryRepositoryPort;
import com.stock.core.domain.port.ClockPort;
import com.stock.core.domain.port.ProductRepositoryPort;
import com.stock.core.domain.valueobject.Money;
import com.stock.core.domain.valueobject.Quantity;
import com.stock.core.domain.valueobject.Sku;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCase {
    private final ProductRepositoryPort products;
    private final CategoryRepositoryPort categories;
    private final ClockPort clock;

    public CreateProductUseCase(ProductRepositoryPort products, CategoryRepositoryPort categories, ClockPort clock) {
        this.products = products;
        this.categories = categories;
        this.clock = clock;
    }

    public ProductDto execute(CreateProductCommand command) {
        if (categories.findById(command.categoryId()).isEmpty()) {
            throw new EntityNotFoundException("Category", command.categoryId());
        }
        Sku sku = new Sku(command.sku());
        if (products.findBySku(sku).isPresent()) {
            throw new DuplicateSkuException(command.sku());
        }
        Product product = new Product(null, sku, command.name(), command.categoryId(),
                new Money(command.price(), "EUR"), new Quantity(command.quantity()), command.alertThreshold(),
                clock.now(), clock.now());
        Product saved = products.save(product);
        return toDto(saved);
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(product.id(), product.sku().value(), product.name(), product.categoryId(),
                product.price().amount(), product.quantity().value(), product.alertThreshold(),
                product.createdAt(), product.updatedAt());
    }
}

package com.stock.application.usecase;

import com.stock.application.dto.response.ProductDto;
import com.stock.core.domain.port.ProductRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListProductsUseCase {
    private final ProductRepositoryPort repository;

    public ListProductsUseCase(ProductRepositoryPort repository) {
        this.repository = repository;
    }

    public List<ProductDto> execute() {
        return repository.findAll().stream().map(product -> new ProductDto(product.id(), product.sku().value(),
                product.name(), product.categoryId(), product.price().amount(), product.quantity().value(),
                product.alertThreshold(), product.createdAt(), product.updatedAt())).toList();
    }
}

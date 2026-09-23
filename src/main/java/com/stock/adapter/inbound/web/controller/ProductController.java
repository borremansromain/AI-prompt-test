package com.stock.adapter.inbound.web.controller;

import com.stock.application.dto.command.CreateProductCommand;
import com.stock.application.dto.response.ProductDto;
import com.stock.application.usecase.CreateProductUseCase;
import com.stock.application.usecase.ListProductsUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final CreateProductUseCase createProduct;
    private final ListProductsUseCase listProducts;

    public ProductController(CreateProductUseCase createProduct, ListProductsUseCase listProducts) {
        this.createProduct = createProduct;
        this.listProducts = listProducts;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STOCK_VIEWER', 'STOCK_MANAGER', 'STOCK_ADMIN')")
    public List<ProductDto> list() { return listProducts.execute(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('STOCK_MANAGER', 'STOCK_ADMIN')")
    public ResponseEntity<ProductDto> create(@Valid @RequestBody CreateProductCommand command) {
        ProductDto product = createProduct.execute(command);
        return ResponseEntity.created(URI.create("/api/v1/products/" + product.id())).body(product);
    }
}

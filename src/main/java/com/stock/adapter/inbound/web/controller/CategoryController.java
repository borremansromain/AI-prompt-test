package com.stock.adapter.inbound.web.controller;

import com.stock.application.dto.command.CreateCategoryCommand;
import com.stock.application.dto.response.CategoryDto;
import com.stock.application.usecase.CreateCategoryUseCase;
import com.stock.application.usecase.ListCategoriesUseCase;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CreateCategoryUseCase createCategory;
    private final ListCategoriesUseCase listCategories;

    public CategoryController(CreateCategoryUseCase createCategory, ListCategoriesUseCase listCategories) {
        this.createCategory = createCategory;
        this.listCategories = listCategories;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STOCK_VIEWER', 'STOCK_MANAGER', 'STOCK_ADMIN')")
    public List<CategoryDto> list() { return listCategories.execute(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('STOCK_MANAGER', 'STOCK_ADMIN')")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CreateCategoryCommand command) {
        CategoryDto category = createCategory.execute(command);
        return ResponseEntity.created(URI.create("/api/v1/categories/" + category.id())).body(category);
    }
}

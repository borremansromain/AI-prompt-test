package com.stock.application.usecase;

import com.stock.application.dto.response.CategoryDto;
import com.stock.core.domain.port.CategoryRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListCategoriesUseCase {
    private final CategoryRepositoryPort repository;

    public ListCategoriesUseCase(CategoryRepositoryPort repository) {
        this.repository = repository;
    }

    public List<CategoryDto> execute() {
        return repository.findAll().stream()
                .map(category -> new CategoryDto(category.id(), category.name(), category.description(),
                        category.createdAt()))
                .toList();
    }
}

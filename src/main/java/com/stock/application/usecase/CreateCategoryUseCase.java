package com.stock.application.usecase;

import com.stock.application.dto.command.CreateCategoryCommand;
import com.stock.application.dto.response.CategoryDto;
import com.stock.core.domain.entity.Category;
import com.stock.core.domain.exception.DomainException;
import com.stock.core.domain.port.CategoryRepositoryPort;
import com.stock.core.domain.port.ClockPort;
import org.springframework.stereotype.Service;

@Service
public class CreateCategoryUseCase {
    private final CategoryRepositoryPort repository;
    private final ClockPort clock;

    public CreateCategoryUseCase(CategoryRepositoryPort repository, ClockPort clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public CategoryDto execute(CreateCategoryCommand command) {
        if (repository.existsByName(command.name())) {
            throw new DomainException("Category name already exists") { };
        }
        Category saved = repository.save(Category.create(command.name(), command.description(), clock.now()));
        return new CategoryDto(saved.id(), saved.name(), saved.description(), saved.createdAt());
    }
}

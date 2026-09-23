package com.stock.core.domain.port;

import com.stock.core.domain.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    List<Category> findAll();
    Optional<Category> findById(UUID id);
    Optional<Category> findByName(String name);
    Category save(Category category);
    void delete(UUID id);
    boolean existsByName(String name);
}

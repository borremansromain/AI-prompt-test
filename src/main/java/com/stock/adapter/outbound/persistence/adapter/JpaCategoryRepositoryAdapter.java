package com.stock.adapter.outbound.persistence.adapter;

import com.stock.adapter.outbound.persistence.entity.CategoryJpaEntity;
import com.stock.adapter.outbound.persistence.repository.CategoryJpaRepository;
import com.stock.core.domain.entity.Category;
import com.stock.core.domain.port.CategoryRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final CategoryJpaRepository repository;

    public JpaCategoryRepositoryAdapter(CategoryJpaRepository repository) { this.repository = repository; }

    public List<Category> findAll() { return repository.findAll().stream().map(this::toDomain).toList(); }
    public Optional<Category> findById(UUID id) { return repository.findById(id).map(this::toDomain); }
    public Optional<Category> findByName(String name) { return repository.findByName(name).map(this::toDomain); }
    public Category save(Category category) {
        return toDomain(repository.save(new CategoryJpaEntity(category.id(), category.name(), category.description(),
                category.createdAt())));
    }
    public void delete(UUID id) { repository.deleteById(id); }
    public boolean existsByName(String name) { return repository.existsByName(name); }

    private Category toDomain(CategoryJpaEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.getDescription(), entity.getCreatedAt());
    }
}

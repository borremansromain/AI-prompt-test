package com.stock.core.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entity, Object id) {
        super(entity + " not found: " + id);
    }
}

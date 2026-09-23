package com.stock.application.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CategoryDto(UUID id, String name, String description, Instant createdAt) {
}

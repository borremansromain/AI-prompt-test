package com.stock.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryCommand(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1000) String description) {
}

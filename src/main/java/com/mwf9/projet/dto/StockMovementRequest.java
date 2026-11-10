package com.mwf9.projet.dto;

import com.mwf9.projet.model.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockMovementRequest(
        @NotNull(message = "Product id is required") Long productId,
        @NotNull(message = "Movement type (IN or OUT) is required") MovementType type,
        @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity,
        String reason) {
}

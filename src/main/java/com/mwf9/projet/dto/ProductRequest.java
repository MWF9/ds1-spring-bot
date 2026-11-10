package com.mwf9.projet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record ProductRequest(
        @NotBlank(message = "Product name is required") String name,
        @NotBlank(message = "SKU is required") String sku,
        @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") BigDecimal price,
        @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity cannot be negative") Integer stockQuantity,
        @NotNull(message = "Reorder level is required") @Min(value = 0, message = "Reorder level cannot be negative") Integer reorderLevel,
        @NotNull(message = "Category id is required") Long categoryId,
        Set<Long> supplierIds) {
}

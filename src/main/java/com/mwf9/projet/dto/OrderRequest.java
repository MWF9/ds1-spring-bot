package com.mwf9.projet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "Customer id is required") Long customerId,
        @NotEmpty(message = "An order needs at least one item") @Valid List<OrderItemRequest> items) {
}

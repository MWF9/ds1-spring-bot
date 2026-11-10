package com.mwf9.projet.dto;

import com.mwf9.projet.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull(message = "Status is required") OrderStatus status) {
}

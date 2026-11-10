package com.mwf9.projet.dto;

import jakarta.validation.constraints.NotBlank;

public record ReasonUpdateRequest(@NotBlank(message = "Reason is required") String reason) {
}

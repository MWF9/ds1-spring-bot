package com.mwf9.projet.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Standard JSON error body returned by the API. */
public record ApiError(LocalDateTime timestamp, int status, String error, String message, Map<String, String> details) {
}

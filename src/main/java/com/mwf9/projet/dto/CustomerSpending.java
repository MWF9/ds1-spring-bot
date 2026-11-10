package com.mwf9.projet.dto;

import java.math.BigDecimal;

/** Result of the "top customers" query. */
public record CustomerSpending(Long customerId, String firstName, String lastName, BigDecimal totalSpent) {
}

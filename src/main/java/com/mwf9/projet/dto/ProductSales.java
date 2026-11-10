package com.mwf9.projet.dto;

/** Result of the "best sellers" query. */
public record ProductSales(Long productId, String productName, Long totalSold) {
}

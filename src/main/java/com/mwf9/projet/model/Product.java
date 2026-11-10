package com.mwf9.projet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** A product sold by the shop: belongs to one category, supplied by many suppliers. */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "SKU is required")
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private int stockQuantity;

    @Min(value = 0, message = "Reorder level cannot be negative")
    @Column(nullable = false)
    private int reorderLevel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(name = "product_suppliers",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id"))
    private Set<Supplier> suppliers = new HashSet<>();
}

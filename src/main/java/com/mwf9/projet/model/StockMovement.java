package com.mwf9.projet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** History of stock entries (IN) and exits (OUT) for a product. */
@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 10)
    private MovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime movementDate;

    private String reason;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public StockMovement(MovementType type, int quantity, String reason, Product product) {
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.product = product;
        this.movementDate = LocalDateTime.now();
    }
}

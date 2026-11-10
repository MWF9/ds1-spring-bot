package com.mwf9.projet.repository;

import com.mwf9.projet.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Custom query #1: products whose stock is at or below their reorder level. */
    @Query("select p from Product p where p.stockQuantity <= p.reorderLevel order by p.stockQuantity asc")
    List<Product> findLowStock();

    /** Custom query #2: products of a category within a price range. */
    List<Product> findByCategoryIdAndPriceBetween(Long categoryId, BigDecimal min, BigDecimal max);

    /** Search by (part of) the name, ignoring case. */
    List<Product> findByNameContainingIgnoreCase(String name);
}

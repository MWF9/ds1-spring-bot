package com.mwf9.projet.repository;

import com.mwf9.projet.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /** Custom query #5: all suppliers that supply a given product (ManyToMany navigation). */
    List<Supplier> findByProductsId(Long productId);
}

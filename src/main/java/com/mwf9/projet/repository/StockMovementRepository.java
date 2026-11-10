package com.mwf9.projet.repository;

import com.mwf9.projet.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);
}

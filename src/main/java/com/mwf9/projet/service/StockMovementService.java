package com.mwf9.projet.service;

import com.mwf9.projet.dto.StockMovementRequest;
import com.mwf9.projet.exception.InsufficientStockException;
import com.mwf9.projet.exception.ResourceNotFoundException;
import com.mwf9.projet.model.MovementType;
import com.mwf9.projet.model.Product;
import com.mwf9.projet.model.StockMovement;
import com.mwf9.projet.repository.ProductRepository;
import com.mwf9.projet.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Stock movements are a ledger: creating one changes the product stock,
 * deleting one reverses its effect. Only the reason text can be edited.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockMovementService {

    private final StockMovementRepository movementRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<StockMovement> findAll() {
        return movementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public StockMovement findById(Long id) {
        return movementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("StockMovement", id));
    }

    @Transactional(readOnly = true)
    public List<StockMovement> findByProduct(Long productId) {
        return movementRepository.findByProductIdOrderByMovementDateDesc(productId);
    }

    public StockMovement create(StockMovementRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.productId()));
        return record(product, request.type(), request.quantity(), request.reason());
    }

    /** Applies the movement to the product stock and stores it. Also used by OrderService. */
    public StockMovement record(Product product, MovementType type, int quantity, String reason) {
        if (type == MovementType.OUT) {
            removeStock(product, quantity);
        } else {
            product.setStockQuantity(product.getStockQuantity() + quantity);
        }
        return movementRepository.save(new StockMovement(type, quantity, reason, product));
    }

    /** Stores a movement only (the caller already changed the stock). */
    public StockMovement log(Product product, MovementType type, int quantity, String reason) {
        return movementRepository.save(new StockMovement(type, quantity, reason, product));
    }

    public StockMovement updateReason(Long id, String reason) {
        StockMovement movement = findById(id);
        movement.setReason(reason);
        return movementRepository.save(movement);
    }

    public void delete(Long id) {
        StockMovement movement = findById(id);
        Product product = movement.getProduct();
        if (movement.getType() == MovementType.IN) {
            removeStock(product, movement.getQuantity());
        } else {
            product.setStockQuantity(product.getStockQuantity() + movement.getQuantity());
        }
        movementRepository.delete(movement);
    }

    public void removeStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity(), quantity);
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }
}

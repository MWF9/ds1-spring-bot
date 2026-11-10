package com.mwf9.projet.controller;

import com.mwf9.projet.dto.ReasonUpdateRequest;
import com.mwf9.projet.dto.StockMovementRequest;
import com.mwf9.projet.model.StockMovement;
import com.mwf9.projet.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService movementService;

    @GetMapping
    public List<StockMovement> getAll() {
        return movementService.findAll();
    }

    @GetMapping("/{id}")
    public StockMovement getById(@PathVariable Long id) {
        return movementService.findById(id);
    }

    @GetMapping("/by-product/{productId}")
    public List<StockMovement> getByProduct(@PathVariable Long productId) {
        return movementService.findByProduct(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovement create(@Valid @RequestBody StockMovementRequest request) {
        return movementService.create(request);
    }

    @PutMapping("/{id}")
    public StockMovement updateReason(@PathVariable Long id, @Valid @RequestBody ReasonUpdateRequest request) {
        return movementService.updateReason(id, request.reason());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        movementService.delete(id);
    }
}

package com.mwf9.projet.controller;

import com.mwf9.projet.model.Supplier;
import com.mwf9.projet.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public List<Supplier> getAll() {
        return supplierService.findAll();
    }

    @GetMapping("/{id}")
    public Supplier getById(@PathVariable Long id) {
        return supplierService.findById(id);
    }

    /** Custom query: suppliers of a given product. */
    @GetMapping("/by-product/{productId}")
    public List<Supplier> getByProduct(@PathVariable Long productId) {
        return supplierService.findByProduct(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Supplier create(@Valid @RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        return supplierService.update(id, supplier);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        supplierService.delete(id);
    }
}

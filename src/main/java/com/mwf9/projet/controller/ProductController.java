package com.mwf9.projet.controller;

import com.mwf9.projet.dto.ProductRequest;
import com.mwf9.projet.model.Product;
import com.mwf9.projet.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    /** Custom query: products to reorder (stock <= reorder level). */
    @GetMapping("/low-stock")
    public List<Product> getLowStock() {
        return productService.findLowStock();
    }

    /** Custom query: products of a category within a price range. */
    @GetMapping("/filter")
    public List<Product> filter(@RequestParam Long categoryId,
                                @RequestParam(defaultValue = "0") BigDecimal minPrice,
                                @RequestParam(defaultValue = "999999999") BigDecimal maxPrice) {
        return productService.filter(categoryId, minPrice, maxPrice);
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String name) {
        return productService.searchByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}

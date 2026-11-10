package com.mwf9.projet.service;

import com.mwf9.projet.dto.ProductRequest;
import com.mwf9.projet.exception.ResourceNotFoundException;
import com.mwf9.projet.model.Category;
import com.mwf9.projet.model.Product;
import com.mwf9.projet.model.Supplier;
import com.mwf9.projet.repository.CategoryRepository;
import com.mwf9.projet.repository.ProductRepository;
import com.mwf9.projet.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional(readOnly = true)
    public List<Product> findLowStock() {
        return productRepository.findLowStock();
    }

    @Transactional(readOnly = true)
    public List<Product> filter(Long categoryId, BigDecimal min, BigDecimal max) {
        return productRepository.findByCategoryIdAndPriceBetween(categoryId, min, max);
    }

    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return productRepository.saveAndFlush(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        apply(product, request);
        return productRepository.saveAndFlush(product);
    }

    public void delete(Long id) {
        productRepository.delete(findById(id));
        productRepository.flush(); // 409 if the product appears in orders or stock movements
    }

    private void apply(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
        Set<Supplier> suppliers = new HashSet<>();
        if (request.supplierIds() != null) {
            for (Long supplierId : request.supplierIds()) {
                suppliers.add(supplierRepository.findById(supplierId)
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId)));
            }
        }
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setReorderLevel(request.reorderLevel());
        product.setCategory(category);
        product.setSuppliers(suppliers);
    }
}

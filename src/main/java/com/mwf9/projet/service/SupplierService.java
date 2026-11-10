package com.mwf9.projet.service;

import com.mwf9.projet.exception.ResourceNotFoundException;
import com.mwf9.projet.model.Product;
import com.mwf9.projet.model.Supplier;
import com.mwf9.projet.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Supplier findById(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    @Transactional(readOnly = true)
    public List<Supplier> findByProduct(Long productId) {
        return supplierRepository.findByProductsId(productId);
    }

    public Supplier create(Supplier supplier) {
        supplier.setId(null);
        return supplierRepository.saveAndFlush(supplier);
    }

    public Supplier update(Long id, Supplier data) {
        Supplier supplier = findById(id);
        supplier.setName(data.getName());
        supplier.setEmail(data.getEmail());
        supplier.setPhone(data.getPhone());
        return supplierRepository.saveAndFlush(supplier);
    }

    public void delete(Long id) {
        Supplier supplier = findById(id);
        // The Product side owns the join table, so detach the supplier from its products first.
        for (Product product : supplier.getProducts()) {
            product.getSuppliers().remove(supplier);
        }
        supplierRepository.delete(supplier);
        supplierRepository.flush();
    }
}

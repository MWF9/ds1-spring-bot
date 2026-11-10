package com.mwf9.projet.config;

import com.mwf9.projet.dto.OrderItemRequest;
import com.mwf9.projet.dto.OrderRequest;
import com.mwf9.projet.dto.ProductRequest;
import com.mwf9.projet.model.Category;
import com.mwf9.projet.model.Customer;
import com.mwf9.projet.model.Supplier;
import com.mwf9.projet.repository.CategoryRepository;
import com.mwf9.projet.service.CategoryService;
import com.mwf9.projet.service.CustomerService;
import com.mwf9.projet.service.OrderService;
import com.mwf9.projet.service.ProductService;
import com.mwf9.projet.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** Inserts a small demo data set at startup when the database is empty. */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderService orderService;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }
        Category electronics = category("Electronics", "Phones, laptops and accessories");
        Category office = category("Office", "Stationery and office supplies");

        Supplier techSupply = supplier("TechSupply SARL", "contact@techsupply.tn", "+216 71 000 001");
        Supplier paperWorld = supplier("PaperWorld", "sales@paperworld.tn", "+216 71 000 002");

        var laptop = productService.create(new ProductRequest("Laptop 15\"", "ELEC-001", new BigDecimal("1899.00"),
                12, 5, electronics.getId(), Set.of(techSupply.getId())));
        var mouse = productService.create(new ProductRequest("Wireless mouse", "ELEC-002", new BigDecimal("39.90"),
                3, 10, electronics.getId(), Set.of(techSupply.getId())));
        var notebook = productService.create(new ProductRequest("A4 notebook", "OFF-001", new BigDecimal("4.50"),
                200, 50, office.getId(), Set.of(paperWorld.getId())));
        productService.create(new ProductRequest("Ballpoint pens (box of 50)", "OFF-002", new BigDecimal("12.00"),
                40, 20, office.getId(), Set.of(paperWorld.getId(), techSupply.getId())));

        Customer amine = customer("Amine", "Ben Salah", "amine@example.com", "Tunis");
        Customer sarra = customer("Sarra", "Trabelsi", "sarra@example.com", "Sfax");

        orderService.create(new OrderRequest(amine.getId(), List.of(
                new OrderItemRequest(laptop.getId(), 2), new OrderItemRequest(mouse.getId(), 1))));
        orderService.create(new OrderRequest(sarra.getId(), List.of(
                new OrderItemRequest(notebook.getId(), 20), new OrderItemRequest(mouse.getId(), 1))));
    }

    private Category category(String name, String description) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        return categoryService.create(c);
    }

    private Supplier supplier(String name, String email, String phone) {
        Supplier s = new Supplier();
        s.setName(name);
        s.setEmail(email);
        s.setPhone(phone);
        return supplierService.create(s);
    }

    private Customer customer(String first, String last, String email, String city) {
        Customer c = new Customer();
        c.setFirstName(first);
        c.setLastName(last);
        c.setEmail(email);
        c.setCity(city);
        return customerService.create(c);
    }
}

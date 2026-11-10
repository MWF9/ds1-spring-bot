package com.mwf9.projet.controller;

import com.mwf9.projet.model.CustomerOrder;
import com.mwf9.projet.model.Customer;
import com.mwf9.projet.service.CustomerService;
import com.mwf9.projet.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping
    public List<Customer> getAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    /** All orders of one customer. */
    @GetMapping("/{id}/orders")
    public List<CustomerOrder> getOrders(@PathVariable Long id) {
        customerService.findById(id); // 404 if the customer does not exist
        return orderService.findByCustomer(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody Customer customer) {
        return customerService.create(customer);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        return customerService.update(id, customer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}

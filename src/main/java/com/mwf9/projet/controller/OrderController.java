package com.mwf9.projet.controller;

import com.mwf9.projet.dto.CustomerSpending;
import com.mwf9.projet.dto.OrderRequest;
import com.mwf9.projet.dto.ProductSales;
import com.mwf9.projet.dto.StatusUpdateRequest;
import com.mwf9.projet.model.CustomerOrder;
import com.mwf9.projet.model.OrderStatus;
import com.mwf9.projet.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<CustomerOrder> getAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public CustomerOrder getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    /** Custom query: orders with a status between two dates (ISO format, e.g. 2026-01-01T00:00:00). */
    @GetMapping("/search")
    public List<CustomerOrder> search(@RequestParam OrderStatus status,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return orderService.search(status, from, to);
    }

    /** Custom query: customers ranked by total spent. */
    @GetMapping("/stats/top-customers")
    public List<CustomerSpending> topCustomers(@RequestParam(defaultValue = "5") int limit) {
        return orderService.topCustomers(limit);
    }

    /** Custom query: best-selling products. */
    @GetMapping("/stats/best-sellers")
    public List<ProductSales> bestSellers(@RequestParam(defaultValue = "5") int limit) {
        return orderService.bestSellers(limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder create(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @PutMapping("/{id}/status")
    public CustomerOrder updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}

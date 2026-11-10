package com.mwf9.projet.service;

import com.mwf9.projet.dto.CustomerSpending;
import com.mwf9.projet.dto.OrderItemRequest;
import com.mwf9.projet.dto.OrderRequest;
import com.mwf9.projet.dto.ProductSales;
import com.mwf9.projet.exception.InvalidOperationException;
import com.mwf9.projet.exception.ResourceNotFoundException;
import com.mwf9.projet.model.*;
import com.mwf9.projet.repository.CustomerRepository;
import com.mwf9.projet.repository.OrderItemRepository;
import com.mwf9.projet.repository.OrderRepository;
import com.mwf9.projet.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final StockMovementService stockMovementService;

    @Transactional(readOnly = true)
    public List<CustomerOrder> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CustomerOrder findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> search(OrderStatus status, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findByStatusAndOrderDateBetween(status, from, to);
    }

    @Transactional(readOnly = true)
    public List<CustomerSpending> topCustomers(int limit) {
        return orderRepository.findTopCustomers(OrderStatus.CANCELLED, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<ProductSales> bestSellers(int limit) {
        return orderItemRepository.findBestSellers(OrderStatus.CANCELLED, PageRequest.of(0, limit));
    }

    /** Creates an order, checks and decreases the stock, and logs one OUT movement per line. */
    public CustomerOrder create(OrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.customerId()));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", line.productId()));
            stockMovementService.removeStock(product, line.quantity()); // throws if not enough stock

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(line.quantity());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }
        order.setTotalAmount(total);
        CustomerOrder saved = orderRepository.saveAndFlush(order);

        for (OrderItem item : saved.getItems()) {
            stockMovementService.log(item.getProduct(), MovementType.OUT, item.getQuantity(),
                    "Order #" + saved.getId());
        }
        return saved;
    }

    /** Changes the status. Cancelling an order puts the products back in stock. */
    public CustomerOrder updateStatus(Long id, OrderStatus newStatus) {
        CustomerOrder order = findById(id);
        OrderStatus current = order.getStatus();
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new InvalidOperationException(
                    "Order #" + id + " is " + current + " and its status can no longer be changed");
        }
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order, "Order #" + id + " cancelled");
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /** Deletes an order. If it was still active, its products go back in stock. */
    public void delete(Long id) {
        CustomerOrder order = findById(id);
        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.DELIVERED) {
            restoreStock(order, "Order #" + id + " deleted");
        }
        orderRepository.delete(order);
    }

    private void restoreStock(CustomerOrder order, String reason) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            stockMovementService.log(product, MovementType.IN, item.getQuantity(), reason);
        }
    }
}

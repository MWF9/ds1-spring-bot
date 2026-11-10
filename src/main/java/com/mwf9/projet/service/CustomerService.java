package com.mwf9.projet.service;

import com.mwf9.projet.exception.ResourceNotFoundException;
import com.mwf9.projet.model.Customer;
import com.mwf9.projet.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    public Customer create(Customer customer) {
        customer.setId(null);
        return customerRepository.saveAndFlush(customer);
    }

    public Customer update(Long id, Customer data) {
        Customer customer = findById(id);
        customer.setFirstName(data.getFirstName());
        customer.setLastName(data.getLastName());
        customer.setEmail(data.getEmail());
        customer.setPhone(data.getPhone());
        customer.setCity(data.getCity());
        return customerRepository.saveAndFlush(customer);
    }

    public void delete(Long id) {
        customerRepository.delete(findById(id));
        customerRepository.flush(); // 409 if the customer still has orders
    }
}

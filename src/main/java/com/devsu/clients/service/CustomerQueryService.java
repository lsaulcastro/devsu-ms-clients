package com.devsu.clients.service;

import com.devsu.clients.domain.exception.EntityNotFoundException;
import com.devsu.clients.domain.model.Customer;
import com.devsu.clients.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerQueryService {

    private final CustomerRepository customerRepository;

    public Customer findByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
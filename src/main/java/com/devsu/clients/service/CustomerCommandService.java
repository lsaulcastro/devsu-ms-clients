package com.devsu.clients.service;

import com.devsu.clients.domain.exception.BusinessRuleViolationException;
import com.devsu.clients.domain.exception.EntityNotFoundException;
import com.devsu.clients.domain.model.Customer;
import com.devsu.clients.dto.ChangePasswordRequest;
import com.devsu.clients.dto.CreateCustomerRequest;
import com.devsu.clients.dto.UpdateCustomerRequest;
import com.devsu.clients.event.CustomerEvent;
import com.devsu.clients.event.outbox.OutboxEventRecorder;
import com.devsu.clients.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerCommandService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxEventRecorder outboxEventRecorder;

    public Customer create(CreateCustomerRequest request) {
        Customer.validatePasswordComplexity(request.password());
        ensureCustomerIdIsAvailable(request.customerId());
        ensureIdentificationIsAvailable(request.identification());

        String hashedPassword = passwordEncoder.encode(request.password());

        Customer customer = new Customer(
                request.customerId(),
                hashedPassword,
                true,
                request.name(),
                request.gender(),
                request.age(),
                request.identification(),
                request.address(),
                request.phone()
        );

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: customerId={}", saved.getCustomerId());

        outboxEventRecorder.record(CustomerEvent.created(
                saved.getCustomerId(), saved.getName(), saved.getIdentification()));

        return saved;
    }

    public Customer update(String customerId, UpdateCustomerRequest request) {
        Customer customer = findCustomerOrFail(customerId);

        customer.updateData(
                request.name(),
                request.gender(),
                request.age(),
                request.address(),
                request.phone()
        );

        Customer saved = customerRepository.save(customer);
        log.info("Customer updated: customerId={}", saved.getCustomerId());

        outboxEventRecorder.record(CustomerEvent.updated(
                saved.getCustomerId(), saved.getName(), saved.getIdentification(), saved.isActive()));

        return saved;
    }

    public void changePassword(String customerId, ChangePasswordRequest request) {
        Customer.validatePasswordComplexity(request.newPassword());

        Customer customer = findCustomerOrFail(customerId);
        String newHashedPassword = passwordEncoder.encode(request.newPassword());
        customer.changePassword(newHashedPassword);

        customerRepository.save(customer);
        log.info("Password changed for customerId={}", customer.getCustomerId());
    }

    public void deactivate(String customerId) {
        Customer customer = findCustomerOrFail(customerId);
        customer.deactivate();

        customerRepository.save(customer);
        log.info("Customer deactivated: customerId={}", customerId);

        outboxEventRecorder.record(CustomerEvent.deactivated(customerId));
    }

    private Customer findCustomerOrFail(String customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));
    }

    private void ensureCustomerIdIsAvailable(String customerId) {
        if (customerRepository.existsByCustomerId(customerId)) {
            throw new BusinessRuleViolationException(
                    "CUSTOMER_ID_ALREADY_EXISTS",
                    "Customer with customerId '" + customerId + "' already exists");
        }
    }

    private void ensureIdentificationIsAvailable(String identification) {
        if (customerRepository.existsByIdentification(identification)) {
            throw new BusinessRuleViolationException(
                    "IDENTIFICATION_ALREADY_EXISTS",
                    "A customer with identification '" + identification + "' already exists");
        }
    }
}
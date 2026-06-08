package com.devsu.clients.domain.model;

import com.devsu.clients.domain.exception.BusinessRuleViolationException;
import com.devsu.clients.domain.exception.InvalidDataException;
import com.devsu.clients.domain.validation.DomainValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.Objects;


@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "id")
public class Customer extends Person {

    private static final int CUSTOMER_ID_MAX_LENGTH = 30;
    private static final int PASSWORD_MIN_LENGTH = 4;
    private static final int PASSWORD_HASH_MAX_LENGTH = 100;

    private static final String RULE_CUSTOMER_INACTIVE = "CUSTOMER_INACTIVE";
    private static final String RULE_CUSTOMER_ALREADY_INACTIVE = "CUSTOMER_ALREADY_INACTIVE";
    private static final String RULE_CUSTOMER_ALREADY_ACTIVE = "CUSTOMER_ALREADY_ACTIVE";

    @Column(name = "customer_id", nullable = false, unique = true, length = CUSTOMER_ID_MAX_LENGTH)
    private String customerId;

    @Column(name = "password", nullable = false, length = PASSWORD_HASH_MAX_LENGTH)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;


    protected Customer() {
        // JPA only
    }

    public Customer(String customerId, String hashedPassword, boolean active,
                    String name, Gender gender, int age,
                    String identification, String address, String phone) {
        super(name, gender, age, identification, address, phone);
        validateCustomerFields(customerId, hashedPassword);
        this.customerId = customerId.trim();
        this.password = hashedPassword;
        this.active = active;
    }

    private static void validateCustomerFields(String customerId, String hashedPassword) {
        DomainValidator.requireNotBlank(customerId, "Customer ID");
        DomainValidator.requireMaxLength(customerId, CUSTOMER_ID_MAX_LENGTH, "Customer ID");
        DomainValidator.requireNotBlank(hashedPassword, "Password hash");
        DomainValidator.requireMaxLength(hashedPassword, PASSWORD_HASH_MAX_LENGTH, "Password hash");
    }

    public static void validatePasswordComplexity(String plainPassword) {
        DomainValidator.requireNotBlank(plainPassword, "Password");
        if (plainPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new InvalidDataException(
                    "Password must be at least " + PASSWORD_MIN_LENGTH + " characters long");
        }
    }

    public void changePassword(String newHashedPassword) {
        ensureActive();
        DomainValidator.requireNotBlank(newHashedPassword, "Password hash");
        DomainValidator.requireMaxLength(newHashedPassword, PASSWORD_HASH_MAX_LENGTH, "Password hash");
        this.password = newHashedPassword;
    }

    public void deactivate() {
        if (!this.active) {
            throw new BusinessRuleViolationException(
                    RULE_CUSTOMER_ALREADY_INACTIVE,
                    "Customer " + customerId + " is already inactive");
        }
        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new BusinessRuleViolationException(
                    RULE_CUSTOMER_ALREADY_ACTIVE,
                    "Customer " + customerId + " is already active");
        }
        this.active = true;
    }

    @Override
    public void updateData(String name, Gender gender, int age,
                           String address, String phone) {
        ensureActive();
        super.updateData(name, gender, age, address, phone);
    }

    private void ensureActive() {
        if (!this.active) {
            throw new BusinessRuleViolationException(
                    RULE_CUSTOMER_INACTIVE,
                    "Customer " + customerId + " is inactive and cannot be modified");
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        if (customerId != null && customer.customerId != null) {
            return Objects.equals(customerId, customer.customerId);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return customerId != null ? Objects.hash(customerId) : super.hashCode();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", customerId='" + customerId + '\'' +
                ", name='" + getName() + '\'' +
                ", active=" + active +
                '}';
    }
}
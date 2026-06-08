package com.devsu.clients.domain.model;

import com.devsu.clients.domain.validation.DomainValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Domain entity representing a Person, persisted via JPA.
 * <p>
 * Parent class of Customer (inheritance required by the spec). Uses JOINED
 * inheritance: persons and customers live in separate tables with a FK from
 * customer.id to person.id. This preserves normalization and integrity.
 * <p>
 * <b>Design decisions:</b>
 * <ul>
 *   <li>Rich entity: validations live in constructor + intentional update
 *       methods. JPA's no-arg constructor is protected — invoking it directly
 *       would bypass validation and is reserved for JPA reflection only.</li>
 *   <li>Controlled mutability: no public setters. Updates go through
 *       intention-revealing methods (updateData).</li>
 *   <li>Identity by id (when present). Equals/hashCode based on identity.</li>
 *   <li>Business limits kept as constants visible at this level.</li>
 *   <li>JPA annotations are structural metadata only (schema, type mapping).
 *       Invariant validation lives in the constructor — Bean Validation
 *       annotations are used in request DTOs, not here.</li>
 * </ul>
 */
@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {

    // Business limits — visible at the entity level
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_IDENTIFICATION_LENGTH = 20;
    private static final int MAX_ADDRESS_LENGTH = 200;
    private static final int MAX_PHONE_LENGTH = 20;
    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "identification", nullable = false, unique = true, length = MAX_IDENTIFICATION_LENGTH)
    private String identification;

    @Column(name = "address", nullable = false, length = MAX_ADDRESS_LENGTH)
    private String address;

    @Column(name = "phone", nullable = false, length = MAX_PHONE_LENGTH)
    private String phone;

    /**
     * Required by JPA. Do not invoke directly — use the validating constructor.
     * Bypasses domain invariant validation by design (JPA hydrates fields via
     * reflection after construction, so validation at construction would be
     * counterproductive here).
     */
    protected Person() {
        // JPA only
    }

    /**
     * Public constructor for creating a NEW Person (id assigned by the database).
     */
    public Person(String name, Gender gender, int age,
                  String identification, String address, String phone) {
        validate(name, gender, age, identification, address, phone);

        this.name = name.trim();
        this.gender = gender;
        this.age = age;
        this.identification = identification.trim();
        this.address = address.trim();
        this.phone = phone.trim();
    }

    // ──────────────────────────────────────────────────────────
    // Invariant validations (business rules visible here)
    // ──────────────────────────────────────────────────────────

    private static void validate(String name, Gender gender, int age,
                                 String identification, String address, String phone) {
        DomainValidator.requireNotBlank(name, "Name");
        DomainValidator.requireMaxLength(name, MAX_NAME_LENGTH, "Name");
        DomainValidator.requireNonNull(gender, "Gender");
        DomainValidator.requireInRange(age, MIN_AGE, MAX_AGE, "Age");
        DomainValidator.requireNotBlank(identification, "Identification");
        DomainValidator.requireMaxLength(identification, MAX_IDENTIFICATION_LENGTH, "Identification");
        DomainValidator.requireNotBlank(address, "Address");
        DomainValidator.requireMaxLength(address, MAX_ADDRESS_LENGTH, "Address");
        DomainValidator.requireNotBlank(phone, "Phone");
        DomainValidator.requireMaxLength(phone, MAX_PHONE_LENGTH, "Phone");
    }

    // ──────────────────────────────────────────────────────────
    // Domain behavior
    // ──────────────────────────────────────────────────────────

    /**
     * Updates the person's mutable data. Identity fields (id, identification)
     * cannot be changed once set.
     */
    public void updateData(String name, Gender gender, int age,
                           String address, String phone) {
        DomainValidator.requireNotBlank(name, "Name");
        DomainValidator.requireMaxLength(name, MAX_NAME_LENGTH, "Name");
        DomainValidator.requireNonNull(gender, "Gender");
        DomainValidator.requireInRange(age, MIN_AGE, MAX_AGE, "Age");
        DomainValidator.requireNotBlank(address, "Address");
        DomainValidator.requireMaxLength(address, MAX_ADDRESS_LENGTH, "Address");
        DomainValidator.requireNotBlank(phone, "Phone");
        DomainValidator.requireMaxLength(phone, MAX_PHONE_LENGTH, "Phone");

        this.name = name.trim();
        this.gender = gender;
        this.age = age;
        this.address = address.trim();
        this.phone = phone.trim();
    }

    // ──────────────────────────────────────────────────────────
    // Getters (no public setters: controlled mutability)
    // ──────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getIdentification() {
        return identification;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    // ──────────────────────────────────────────────────────────
    // Identity
    // ──────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        // Identity by id when present
        if (id != null && person.id != null) {
            return Objects.equals(id, person.id);
        }
        // Fallback to natural identity (identification) for new entities
        return Objects.equals(identification, person.identification);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(identification);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identification='" + identification + '\'' +
                '}';
    }
}
package com.devsu.clients.domain.model;

import com.devsu.clients.domain.exception.BusinessRuleViolationException;
import com.devsu.clients.domain.exception.InvalidDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Customer domain entity")
class CustomerTest {

    private static final String VALID_CUSTOMER_ID = "JLEMA";
    private static final String VALID_HASHED_PASSWORD = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
    private static final String VALID_NAME = "Jose Lema";
    private static final String VALID_IDENTIFICATION = "JL-001";
    private static final String VALID_ADDRESS = "Otavalo sn y principal";
    private static final String VALID_PHONE = "098254785";

    private Customer newActiveCustomer() {
        return new Customer(
                VALID_CUSTOMER_ID, VALID_HASHED_PASSWORD, true,
                VALID_NAME, Gender.MALE, 35,
                VALID_IDENTIFICATION, VALID_ADDRESS, VALID_PHONE
        );
    }

    @Nested
    @DisplayName("when constructing")
    class Constructor {

        @Test
        @DisplayName("should create a valid customer with all fields")
        void shouldCreateValidCustomer() {
            Customer customer = newActiveCustomer();

            assertThat(customer.getCustomerId()).isEqualTo(VALID_CUSTOMER_ID);
            assertThat(customer.getName()).isEqualTo(VALID_NAME);
            assertThat(customer.getGender()).isEqualTo(Gender.MALE);
            assertThat(customer.getAge()).isEqualTo(35);
            assertThat(customer.getIdentification()).isEqualTo(VALID_IDENTIFICATION);
            assertThat(customer.getAddress()).isEqualTo(VALID_ADDRESS);
            assertThat(customer.getPhone()).isEqualTo(VALID_PHONE);
            assertThat(customer.isActive()).isTrue();
            assertThat(customer.getPassword()).isEqualTo(VALID_HASHED_PASSWORD);
        }

        @Test
        @DisplayName("should reject blank customerId")
        void shouldRejectBlankCustomerId() {
            assertThatThrownBy(() -> new Customer(
                    "  ", VALID_HASHED_PASSWORD, true,
                    VALID_NAME, Gender.MALE, 35,
                    VALID_IDENTIFICATION, VALID_ADDRESS, VALID_PHONE
            )).isInstanceOf(InvalidDataException.class)
              .hasMessageContaining("Customer ID");
        }

        @Test
        @DisplayName("should reject blank hashed password")
        void shouldRejectBlankPassword() {
            assertThatThrownBy(() -> new Customer(
                    VALID_CUSTOMER_ID, "", true,
                    VALID_NAME, Gender.MALE, 35,
                    VALID_IDENTIFICATION, VALID_ADDRESS, VALID_PHONE
            )).isInstanceOf(InvalidDataException.class)
              .hasMessageContaining("Password");
        }

        @Test
        @DisplayName("should reject inherited Person invariant violations (negative age)")
        void shouldRejectNegativeAge() {
            assertThatThrownBy(() -> new Customer(
                    VALID_CUSTOMER_ID, VALID_HASHED_PASSWORD, true,
                    VALID_NAME, Gender.MALE, -1,
                    VALID_IDENTIFICATION, VALID_ADDRESS, VALID_PHONE
            )).isInstanceOf(InvalidDataException.class)
              .hasMessageContaining("Age");
        }

        @Test
        @DisplayName("should reject inherited Person invariant violations (null gender)")
        void shouldRejectNullGender() {
            assertThatThrownBy(() -> new Customer(
                    VALID_CUSTOMER_ID, VALID_HASHED_PASSWORD, true,
                    VALID_NAME, null, 35,
                    VALID_IDENTIFICATION, VALID_ADDRESS, VALID_PHONE
            )).isInstanceOf(InvalidDataException.class)
              .hasMessageContaining("Gender");
        }

        @Test
        @DisplayName("should reject customerId longer than 30 characters")
        void shouldRejectTooLongCustomerId() {
            String tooLong = "X".repeat(31);

            assertThatThrownBy(() -> new Customer(
                    tooLong, VALID_HASHED_PASSWORD, true,
                    VALID_NAME, Gender.MALE, 35,
                    VALID_IDENTIFICATION, VALID_ADDRESS, VALID_PHONE
            )).isInstanceOf(InvalidDataException.class)
              .hasMessageContaining("Customer ID");
        }
    }

    @Nested
    @DisplayName("validatePasswordComplexity (static rule)")
    class PasswordComplexity {

        @Test
        @DisplayName("should accept passwords with 4 or more characters")
        void shouldAcceptValidPassword() {
            // No exception means valid
            Customer.validatePasswordComplexity("1234");
            Customer.validatePasswordComplexity("a_long_secure_password_2026");
        }

        @Test
        @DisplayName("should reject blank password")
        void shouldRejectBlankPassword() {
            assertThatThrownBy(() -> Customer.validatePasswordComplexity("   "))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Password");
        }

        @Test
        @DisplayName("should reject password shorter than 4 characters")
        void shouldRejectShortPassword() {
            assertThatThrownBy(() -> Customer.validatePasswordComplexity("abc"))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("at least 4 characters");
        }
    }

    @Nested
    @DisplayName("deactivate / activate transitions")
    class StateTransitions {

        @Test
        @DisplayName("should deactivate an active customer")
        void shouldDeactivateActiveCustomer() {
            Customer customer = newActiveCustomer();

            customer.deactivate();

            assertThat(customer.isActive()).isFalse();
        }

        @Test
        @DisplayName("should fail to deactivate an already inactive customer")
        void shouldRejectDeactivatingInactiveCustomer() {
            Customer customer = newActiveCustomer();
            customer.deactivate();

            assertThatThrownBy(customer::deactivate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .extracting(ex -> ((BusinessRuleViolationException) ex).getRuleCode())
                    .isEqualTo("CUSTOMER_ALREADY_INACTIVE");
        }

        @Test
        @DisplayName("should activate an inactive customer")
        void shouldActivateInactiveCustomer() {
            Customer customer = newActiveCustomer();
            customer.deactivate();

            customer.activate();

            assertThat(customer.isActive()).isTrue();
        }

        @Test
        @DisplayName("should fail to activate an already active customer")
        void shouldRejectActivatingActiveCustomer() {
            Customer customer = newActiveCustomer();

            assertThatThrownBy(customer::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .extracting(ex -> ((BusinessRuleViolationException) ex).getRuleCode())
                    .isEqualTo("CUSTOMER_ALREADY_ACTIVE");
        }
    }

    @Nested
    @DisplayName("updateData behavior")
    class UpdateData {

        @Test
        @DisplayName("should update mutable fields of an active customer")
        void shouldUpdateMutableFields() {
            Customer customer = newActiveCustomer();

            customer.updateData("Updated Name", Gender.FEMALE, 40, "New Address", "8095559999");

            assertThat(customer.getName()).isEqualTo("Updated Name");
            assertThat(customer.getGender()).isEqualTo(Gender.FEMALE);
            assertThat(customer.getAge()).isEqualTo(40);
            assertThat(customer.getAddress()).isEqualTo("New Address");
            assertThat(customer.getPhone()).isEqualTo("8095559999");
        }

        @Test
        @DisplayName("should NOT change identity fields (customerId, identification)")
        void shouldPreserveIdentityFields() {
            Customer customer = newActiveCustomer();
            String originalCustomerId = customer.getCustomerId();
            String originalIdentification = customer.getIdentification();

            customer.updateData("Updated Name", Gender.FEMALE, 40, "New Address", "8095559999");

            assertThat(customer.getCustomerId()).isEqualTo(originalCustomerId);
            assertThat(customer.getIdentification()).isEqualTo(originalIdentification);
        }

        @Test
        @DisplayName("should reject update on an inactive customer")
        void shouldRejectUpdateOnInactiveCustomer() {
            Customer customer = newActiveCustomer();
            customer.deactivate();

            assertThatThrownBy(() ->
                    customer.updateData("X", Gender.MALE, 35, "X", "X")
            ).isInstanceOf(BusinessRuleViolationException.class)
             .extracting(ex -> ((BusinessRuleViolationException) ex).getRuleCode())
             .isEqualTo("CUSTOMER_INACTIVE");
        }

        @Test
        @DisplayName("should reject update with invalid data (blank name)")
        void shouldRejectUpdateWithInvalidData() {
            Customer customer = newActiveCustomer();

            assertThatThrownBy(() ->
                    customer.updateData("", Gender.MALE, 35, "X", "X")
            ).isInstanceOf(InvalidDataException.class)
             .hasMessageContaining("Name");
        }
    }

    @Nested
    @DisplayName("changePassword behavior")
    class ChangePassword {

        @Test
        @DisplayName("should change password of an active customer")
        void shouldChangePasswordWhenActive() {
            Customer customer = newActiveCustomer();
            String newHash = "$2a$10$newHashValueForTestingPurposes12345678901234";

            customer.changePassword(newHash);

            assertThat(customer.getPassword()).isEqualTo(newHash);
        }

        @Test
        @DisplayName("should reject changing password on an inactive customer")
        void shouldRejectChangePasswordWhenInactive() {
            Customer customer = newActiveCustomer();
            customer.deactivate();

            assertThatThrownBy(() -> customer.changePassword("$2a$10$validHash"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .extracting(ex -> ((BusinessRuleViolationException) ex).getRuleCode())
                    .isEqualTo("CUSTOMER_INACTIVE");
        }

        @Test
        @DisplayName("should reject blank password hash")
        void shouldRejectBlankPasswordHash() {
            Customer customer = newActiveCustomer();

            assertThatThrownBy(() -> customer.changePassword("  "))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Password");
        }
    }

    @Nested
    @DisplayName("equals and hashCode (identity)")
    class Identity {

        @Test
        @DisplayName("two customers with same customerId should be equal")
        void shouldBeEqualBySameCustomerId() {
            Customer c1 = newActiveCustomer();
            Customer c2 = new Customer(
                    VALID_CUSTOMER_ID, "different-hash", false,
                    "Different Name", Gender.FEMALE, 50,
                    "different-id", "different-address", "different-phone"
            );

            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        @DisplayName("two customers with different customerId should NOT be equal")
        void shouldNotBeEqualByDifferentCustomerId() {
            Customer c1 = newActiveCustomer();
            Customer c2 = new Customer(
                    "OTHER", VALID_HASHED_PASSWORD, true,
                    VALID_NAME, Gender.MALE, 35,
                    "OTHER-ID", VALID_ADDRESS, VALID_PHONE
            );

            assertThat(c1).isNotEqualTo(c2);
        }
    }
}
package domain.factory;

import domain.model.AllergyProfile;
import domain.model.Customer;
import domain.model.OperationResult;
import java.math.BigDecimal;

/**
 * Builds Customer objects step by step and validates required customer fields before creation.
 */
public class CustomerBuilder {
    private String id;
    private String name;
    private String email;
    private AllergyProfile allergyProfile;
    private BigDecimal walletBalance = BigDecimal.valueOf(500);

    public CustomerBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public CustomerBuilder withAllergyProfile(AllergyProfile allergyProfile) {
        this.allergyProfile = allergyProfile;
        return this;
    }

    public CustomerBuilder withWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
        return this;
    }

    public OperationResult<Customer> build() {
        if (isBlank(id)) {
            return OperationResult.failure("Customer id cannot be empty.");
        }
        if (isBlank(name)) {
            return OperationResult.failure("Customer name cannot be empty.");
        }
        if (isBlank(email)) {
            return OperationResult.failure("Customer email cannot be empty.");
        }
        if (allergyProfile == null) {
            return OperationResult.failure("Allergy profile cannot be null.");
        }
        if (walletBalance == null || walletBalance.compareTo(BigDecimal.ZERO) < 0) {
            return OperationResult.failure("Wallet balance cannot be negative.");
        }
        return OperationResult.success("Customer created.", new Customer(id, name, email, allergyProfile, walletBalance));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

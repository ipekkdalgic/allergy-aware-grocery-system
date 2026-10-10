package domain.model;

import java.math.BigDecimal;

/**
 * Represents a registered customer with an allergy profile and wallet balance.
 */
public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final AllergyProfile allergyProfile;
    private BigDecimal walletBalance;

    public Customer(String id, String name, String email, AllergyProfile allergyProfile, BigDecimal walletBalance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.allergyProfile = allergyProfile;
        this.walletBalance = walletBalance == null ? BigDecimal.ZERO : walletBalance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public AllergyProfile getAllergyProfile() {
        return allergyProfile;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public boolean hasEnoughWalletBalance(BigDecimal amount) {
        return walletBalance.compareTo(amount) >= 0;
    }

    public void deductFromWallet(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (!hasEnoughWalletBalance(amount)) {
            throw new IllegalStateException("Insufficient wallet balance.");
        }
        walletBalance = walletBalance.subtract(amount);
    }
}

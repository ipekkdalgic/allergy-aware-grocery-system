package application.service;

import domain.enums.PaymentMethod;
import domain.gateway.PaymentGateway;
import domain.model.Customer;
import domain.model.PaymentRequest;
import domain.model.PaymentResult;
import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Validates and processes payment requests.
 * Wallet payments are handled internally, while card payments are delegated to the payment gateway abstraction.
 */
public class PaymentService {
    private final PaymentGateway paymentGateway;

    public PaymentService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public PaymentResult processPayment(Customer customer, PaymentRequest request) {
        // Payment information is validated before any wallet deduction or gateway call.
        PaymentResult validation = validatePayment(customer, request);
        if (!validation.isApproved()) {
            return validation;
        }

        // Wallet balance payment is handled inside the application because it does not need the external gateway.
        if (request.getPaymentMethod() == PaymentMethod.WALLET_BALANCE) {
            customer.deductFromWallet(request.getAmount());
            String transactionId = "WALLET-" + System.currentTimeMillis();
            return PaymentResult.approved(transactionId, "WALLET-AUTH", "Wallet payment approved.");
        }

        // Card payments are processed through the gateway abstraction.
        return paymentGateway.process(request);
    }

    private PaymentResult validatePayment(Customer customer, PaymentRequest request) {
        if (customer == null) {
            return PaymentResult.failed("Payment failed. Reason: Customer must be logged in.");
        }
        if (request == null) {
            return PaymentResult.failed("Payment failed. Reason: Payment request cannot be empty.");
        }
        if (request.getPaymentMethod() == null) {
            return PaymentResult.failed("Payment failed. Reason: Payment method must be selected.");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failed("Payment failed. Reason: Payment amount must be greater than zero.");
        }

        if (request.getPaymentMethod() == PaymentMethod.WALLET_BALANCE) {
            if (!customer.hasEnoughWalletBalance(request.getAmount())) {
                return PaymentResult.declined("Payment declined. Reason: Insufficient wallet balance. Current balance: " + customer.getWalletBalance() + " TRY");
            }
            return PaymentResult.approved("VALIDATION", "VALIDATION", "Wallet validation successful.");
        }

        if (isBlank(request.getCardHolderName())) {
            return PaymentResult.failed("Payment failed. Reason: Card holder name cannot be empty.");
        }
        if (!isDigits(request.getCardNumber()) || request.getCardNumber().length() != 16) {
            return PaymentResult.failed("Payment failed. Reason: Card number must contain exactly 16 digits.");
        }
        if (request.getExpiryMonth() < 1 || request.getExpiryMonth() > 12) {
            return PaymentResult.failed("Payment failed. Reason: Expiry month must be between 1 and 12.");
        }
        YearMonth expiry = YearMonth.of(request.getExpiryYear(), request.getExpiryMonth());
        if (expiry.isBefore(YearMonth.now())) {
            return PaymentResult.failed("Payment failed. Reason: Card is expired.");
        }
        if (!isDigits(request.getCvv()) || request.getCvv().length() != 3) {
            return PaymentResult.failed("Payment failed. Reason: CVV must contain exactly 3 digits.");
        }
        return PaymentResult.approved("VALIDATION", "VALIDATION", "Card validation successful.");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isDigits(String value) {
        return value != null && value.matches("\\d+");
    }
}

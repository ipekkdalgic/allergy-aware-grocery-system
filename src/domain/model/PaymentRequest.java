package domain.model;

import domain.enums.PaymentMethod;
import java.math.BigDecimal;

public class PaymentRequest {
    private final PaymentMethod paymentMethod;
    private final BigDecimal amount;
    private final String cardHolderName;
    private final String cardNumber;
    private final int expiryMonth;
    private final int expiryYear;
    private final String cvv;

    private PaymentRequest(PaymentMethod paymentMethod, BigDecimal amount, String cardHolderName,
                           String cardNumber, int expiryMonth, int expiryYear, String cvv) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
    }

    public static PaymentRequest card(PaymentMethod paymentMethod, BigDecimal amount, String cardHolderName,
                                      String cardNumber, int expiryMonth, int expiryYear, String cvv) {
        return new PaymentRequest(paymentMethod, amount, cardHolderName, cardNumber, expiryMonth, expiryYear, cvv);
    }

    public static PaymentRequest wallet(BigDecimal amount) {
        return new PaymentRequest(PaymentMethod.WALLET_BALANCE, amount, null, null, 0, 0, null);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public String getCvv() {
        return cvv;
    }
}

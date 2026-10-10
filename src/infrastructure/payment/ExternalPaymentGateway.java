package infrastructure.payment;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

//Simulates an external card payment provider for credit and debit card payments.
public class ExternalPaymentGateway {
    private final AtomicInteger transactionCounter = new AtomicInteger(1000);

    public ExternalPaymentResponse makeCardPayment(String methodName, String cardNumber, BigDecimal amount) {
        if (cardNumber == null || cardNumber.endsWith("0000")) {
            return new ExternalPaymentResponse(false, null, null, "External gateway declined the card.");
        }
        String transactionId = "TXN-" + transactionCounter.incrementAndGet();
        String authorizationCode = "AUTH-" + Math.abs((cardNumber + amount).hashCode());
        return new ExternalPaymentResponse(true, transactionId, authorizationCode, methodName + " payment approved by external gateway.");
    }
}

package domain.model;

import domain.enums.PaymentStatus;

public class PaymentResult {
    private final PaymentStatus status;
    private final String transactionId;
    private final String authorizationCode;
    private final String message;

    public PaymentResult(PaymentStatus status, String transactionId, String authorizationCode, String message) {
        this.status = status;
        this.transactionId = transactionId;
        this.authorizationCode = authorizationCode;
        this.message = message;
    }

    public static PaymentResult approved(String transactionId, String authorizationCode, String message) {
        return new PaymentResult(PaymentStatus.APPROVED, transactionId, authorizationCode, message);
    }

    public static PaymentResult declined(String message) {
        return new PaymentResult(PaymentStatus.DECLINED, null, null, message);
    }

    public static PaymentResult failed(String message) {
        return new PaymentResult(PaymentStatus.FAILED, null, null, message);
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }
}

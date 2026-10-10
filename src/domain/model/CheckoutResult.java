package domain.model;

public class CheckoutResult {
    private final boolean success;
    private final String message;
    private final Order order;
    private final PaymentResult paymentResult;

    private CheckoutResult(boolean success, String message, Order order, PaymentResult paymentResult) {
        this.success = success;
        this.message = message;
        this.order = order;
        this.paymentResult = paymentResult;
    }

    public static CheckoutResult success(String message, Order order, PaymentResult paymentResult) {
        return new CheckoutResult(true, message, order, paymentResult);
    }

    public static CheckoutResult failure(String message) {
        return new CheckoutResult(false, message, null, null);
    }

    public static CheckoutResult failure(String message, Order order, PaymentResult paymentResult) {
        return new CheckoutResult(false, message, order, paymentResult);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Order getOrder() {
        return order;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }
}

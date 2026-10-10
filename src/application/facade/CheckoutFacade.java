package application.facade;

import application.service.CartService;
import application.service.OrderService;
import application.service.PaymentService;
import domain.model.Cart;
import domain.model.CheckoutResult;
import domain.model.Customer;
import domain.model.OperationResult;
import domain.model.Order;
import domain.model.PaymentRequest;
import domain.model.PaymentResult;

/**
 * Provides a simplified checkout operation for the presentation layer.
 * It coordinates cart validation, order creation, payment, stock update, and cart clearing.
 */
public class CheckoutFacade {
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public CheckoutFacade(CartService cartService, OrderService orderService, PaymentService paymentService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    public CheckoutResult checkout(Customer customer, PaymentRequest paymentRequest, boolean allergyWarningsConfirmed) {
        if (customer == null) {
            return CheckoutResult.failure("Checkout failed. Reason: Please login first.");
        }

        // Checkout starts from the current cart of the logged-in customer.
        OperationResult<Cart> cartResult = cartService.getCart(customer);
        if (!cartResult.isSuccess()) {
            return CheckoutResult.failure(cartResult.getMessage());
        }
        Cart cart = cartResult.getData();

        // Cart is validated before order creation so stock and active-product rules are protected.
        OperationResult<Void> cartValidation = cartService.validateCartForCheckout(cart);
        if (!cartValidation.isSuccess()) {
            return CheckoutResult.failure(cartValidation.getMessage());
        }

        // Allergy warnings must be confirmed again before the final purchase step.
        if (cart.hasAllergyWarnings() && !allergyWarningsConfirmed) {
            return CheckoutResult.failure("Checkout cancelled. Allergy warnings were not confirmed.");
        }

        // The order is created before payment so failed payments can be recorded as cancelled orders.
        OperationResult<Order> orderResult = orderService.createOrderFromCart(cart);
        if (!orderResult.isSuccess()) {
            return CheckoutResult.failure(orderResult.getMessage());
        }
        Order order = orderResult.getData();

        // Payment must be approved before stock is decreased and the cart is cleared.
        PaymentResult paymentResult = paymentService.processPayment(customer, paymentRequest);
        if (!paymentResult.isApproved()) {
            orderService.moveToStatus(order, domain.enums.OrderStatus.CANCELLED);
            return CheckoutResult.failure("Checkout failed. " + paymentResult.getMessage(), order, paymentResult);
        }

        OperationResult<Void> paymentStatusResult = orderService.moveToStatus(order, domain.enums.OrderStatus.PAID);
        if (!paymentStatusResult.isSuccess()) {
            return CheckoutResult.failure(paymentStatusResult.getMessage(), order, paymentResult);
        }

        // These updates happen only after successful payment and PAID status transition.
        cartService.decreaseStockAfterCheckout(cart);
        cartService.clearCart(cart);
        return CheckoutResult.success("Checkout completed successfully.", order, paymentResult);
    }
}

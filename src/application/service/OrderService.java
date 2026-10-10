package application.service;

import domain.enums.OrderStatus;
import domain.model.Cart;
import domain.model.CartItem;
import domain.model.Money;
import domain.model.OperationResult;
import domain.model.Order;
import domain.model.OrderItem;
import domain.observer.CustomerNotificationObserver;
import domain.observer.OrderLogObserver;
import domain.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates orders and controls order status changes through the State pattern.
 * It also attaches observers so that status changes can create notifications and log entries.
 */
public class OrderService {
    private final OrderRepository orderRepository;
    private final AtomicInteger orderCounter = new AtomicInteger(0);

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OperationResult<Order> createOrderFromCart(Cart cart) {
        if (cart == null || cart.isEmpty()) {
            return OperationResult.failure("Order cannot be created from an empty cart.");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        Money total = Money.zero();
        // Cart items are copied into order items so the order keeps its checkout-time snapshot.
        for (CartItem cartItem : cart.getItems()) {
            orderItems.add(new OrderItem(cartItem));
            total = total.add(cartItem.getLineTotal());
        }
        String orderId = String.format("O%03d", orderCounter.incrementAndGet());
        Order order = new Order(orderId, cart.getCustomerId(), orderItems, total);
        // Observers are attached once, then notified by the Order object after valid state transitions.
        order.addObserver(new CustomerNotificationObserver());
        order.addObserver(new OrderLogObserver());
        orderRepository.save(order);
        return OperationResult.success("Order created successfully.", order);
    }

    public OperationResult<Void> moveToStatus(Order order, OrderStatus targetStatus) {
        if (order == null) {
            return OperationResult.failure("Order cannot be null.");
        }
        if (targetStatus == null) {
            return OperationResult.failure("Target status cannot be null.");
        }

        OperationResult<Void> result;
        // The service selects the requested lifecycle operation; the current OrderState validates it.
        switch (targetStatus) {
            case PAID:
                result = order.pay();
                break;
            case PREPARING:
                result = order.prepare();
                break;
            case SHIPPED:
                result = order.ship();
                break;
            case DELIVERED:
                result = order.deliver();
                break;
            case CANCELLED:
                result = order.cancel();
                break;
            default:
                result = OperationResult.failure("Unsupported target status.");
        }
        if (result.isSuccess()) {
            orderRepository.save(order);
        }
        return result;
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}

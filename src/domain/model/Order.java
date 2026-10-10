package domain.model;

import domain.enums.OrderStatus;
import domain.observer.OrderObserver;
import domain.state.CreatedState;
import domain.state.OrderState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a completed checkout record and owns the order lifecycle state.
 * Observers are notified whenever the order status changes successfully.
 */
public class Order {
    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private OrderStatus status;
    private OrderState state;
    private final List<OrderObserver> observers = new ArrayList<>();
    private final List<String> notifications = new ArrayList<>();

    public Order(String id, String customerId, List<OrderItem> items, Money totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.CREATED;
        this.state = new CreatedState();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderStatus> getAvailableNextStatuses() {
        return state.getAvailableNextStatuses();
    }

    public void addObserver(OrderObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void addNotification(String notification) {
        if (notification != null && !notification.trim().isEmpty()) {
            notifications.add(notification);
        }
    }

    public List<String> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    public OperationResult<Void> pay() {
        return state.pay(this);
    }

    public OperationResult<Void> prepare() {
        return state.prepare(this);
    }

    public OperationResult<Void> ship() {
        return state.ship(this);
    }

    public OperationResult<Void> deliver() {
        return state.deliver(this);
    }

    public OperationResult<Void> cancel() {
        return state.cancel(this);
    }

    public void transitionTo(OrderStatus newStatus, OrderState newState) {
        // State transition is centralized here so observers are notified consistently.
        OrderStatus oldStatus = this.status;
        this.status = newStatus;
        this.state = newState;
        notifyObservers(oldStatus, newStatus);
    }

    private void notifyObservers(OrderStatus oldStatus, OrderStatus newStatus) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(this, oldStatus, newStatus);
        }
    }
}

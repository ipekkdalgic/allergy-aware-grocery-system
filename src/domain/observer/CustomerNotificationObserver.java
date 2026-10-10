package domain.observer;

import domain.enums.OrderStatus;
import domain.model.Order;

/**
 * Adds a customer-facing notification message when an order status changes.
 */
public class CustomerNotificationObserver implements OrderObserver {
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        order.addNotification("Customer notification: Order " + order.getId() + " changed from " + oldStatus + " to " + newStatus + ".");
    }
}

package domain.observer;

import domain.enums.OrderStatus;
import domain.model.Order;

/**
 * Adds an internal log message when an order status changes.
 */
public class OrderLogObserver implements OrderObserver {
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        order.addNotification("Order log: status transition recorded as " + oldStatus + " -> " + newStatus + ".");
    }
}

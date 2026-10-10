package domain.observer;

import domain.enums.OrderStatus;
import domain.model.Order;

//Defines the observer contract for actions triggered after order status changes.
public interface OrderObserver {
    void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}

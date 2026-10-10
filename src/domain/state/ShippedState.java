package domain.state;

import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import java.util.Collections;
import java.util.List;

/**
 * Represents the SHIPPED order state.
 * A shipped order can move only to DELIVERED.
 */
public class ShippedState extends AbstractOrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.SHIPPED;
    }

    @Override
    public List<OrderStatus> getAvailableNextStatuses() {
        return Collections.singletonList(OrderStatus.DELIVERED);
    }

    @Override
    public OperationResult<Void> deliver(Order order) {
        order.transitionTo(OrderStatus.DELIVERED, new DeliveredState());
        return OperationResult.success("Order delivered successfully.");
    }
}

package domain.state;

import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the PREPARING order state.
 * A preparing order can move to SHIPPED or be CANCELLED.
 */
public class PreparingState extends AbstractOrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PREPARING;
    }

    @Override
    public List<OrderStatus> getAvailableNextStatuses() {
        return Arrays.asList(OrderStatus.SHIPPED, OrderStatus.CANCELLED);
    }

    @Override
    public OperationResult<Void> ship(Order order) {
        order.transitionTo(OrderStatus.SHIPPED, new ShippedState());
        return OperationResult.success("Order shipped successfully.");
    }

    @Override
    public OperationResult<Void> cancel(Order order) {
        order.transitionTo(OrderStatus.CANCELLED, new CancelledState());
        return OperationResult.success("Order cancelled successfully.");
    }
}

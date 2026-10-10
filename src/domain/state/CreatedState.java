package domain.state;

import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the CREATED order state.
 * A created order can become PAID or CANCELLED.
 */
public class CreatedState extends AbstractOrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CREATED;
    }

    @Override
    public List<OrderStatus> getAvailableNextStatuses() {
        return Arrays.asList(OrderStatus.PAID, OrderStatus.CANCELLED);
    }

    @Override
    public OperationResult<Void> pay(Order order) {
        order.transitionTo(OrderStatus.PAID, new PaidState());
        return OperationResult.success("Order paid successfully.");
    }

    @Override
    public OperationResult<Void> cancel(Order order) {
        order.transitionTo(OrderStatus.CANCELLED, new CancelledState());
        return OperationResult.success("Order cancelled successfully.");
    }
}

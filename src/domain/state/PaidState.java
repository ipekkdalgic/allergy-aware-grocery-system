package domain.state;

import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the PAID order state.
 * A paid order can move to PREPARING or be CANCELLED.
 */
public class PaidState extends AbstractOrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PAID;
    }

    @Override
    public List<OrderStatus> getAvailableNextStatuses() {
        return Arrays.asList(OrderStatus.PREPARING, OrderStatus.CANCELLED);
    }

    @Override
    public OperationResult<Void> prepare(Order order) {
        order.transitionTo(OrderStatus.PREPARING, new PreparingState());
        return OperationResult.success("Order moved to PREPARING.");
    }

    @Override
    public OperationResult<Void> cancel(Order order) {
        order.transitionTo(OrderStatus.CANCELLED, new CancelledState());
        return OperationResult.success("Order cancelled successfully.");
    }
}

package domain.state;

import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import java.util.Collections;
import java.util.List;

/**
 * Provides default failure behavior for invalid state transitions.
 * Concrete states override only the transitions that are valid for them.
 */
public abstract class AbstractOrderState implements OrderState {
    @Override
    public List<OrderStatus> getAvailableNextStatuses() {
        return Collections.emptyList();
    }

    @Override
    public OperationResult<Void> pay(Order order) {
        return invalid(order, OrderStatus.PAID);
    }

    @Override
    public OperationResult<Void> prepare(Order order) {
        return invalid(order, OrderStatus.PREPARING);
    }

    @Override
    public OperationResult<Void> ship(Order order) {
        return invalid(order, OrderStatus.SHIPPED);
    }

    @Override
    public OperationResult<Void> deliver(Order order) {
        return invalid(order, OrderStatus.DELIVERED);
    }

    @Override
    public OperationResult<Void> cancel(Order order) {
        return invalid(order, OrderStatus.CANCELLED);
    }

    protected OperationResult<Void> invalid(Order order, OrderStatus requestedStatus) {
        return OperationResult.failure("Invalid status transition. A " + order.getStatus() + " order cannot move to " + requestedStatus + ".");
    }
}

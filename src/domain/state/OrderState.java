package domain.state;

import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import java.util.List;

//Defines valid order lifecycle operations for each concrete order state.
public interface OrderState {
    OrderStatus getStatus();
    List<OrderStatus> getAvailableNextStatuses();
    OperationResult<Void> pay(Order order);
    OperationResult<Void> prepare(Order order);
    OperationResult<Void> ship(Order order);
    OperationResult<Void> deliver(Order order);
    OperationResult<Void> cancel(Order order);
}

package domain.state;

import domain.enums.OrderStatus;

/**
 * Represents the final CANCELLED order state.
 * No further transition is allowed from this state.
 */
public class CancelledState extends AbstractOrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELLED;
    }
}

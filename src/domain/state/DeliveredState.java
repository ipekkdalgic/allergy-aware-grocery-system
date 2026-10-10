package domain.state;

import domain.enums.OrderStatus;

/**
 * Represents the final DELIVERED order state.
 * No further transition is allowed from this state.
 */
public class DeliveredState extends AbstractOrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DELIVERED;
    }
}

package domain.gateway;

import domain.model.PaymentRequest;
import domain.model.PaymentResult;

/**
 * Represents the internal payment gateway abstraction used by PaymentService.
 */
public interface PaymentGateway {
    PaymentResult process(PaymentRequest request);
}

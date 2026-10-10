package infrastructure.payment;

import domain.gateway.PaymentGateway;
import domain.model.PaymentRequest;
import domain.model.PaymentResult;

//Adapts the simulated external payment gateway to the internal PaymentGateway interface.
public class PaymentGatewayAdapter implements PaymentGateway {
    private final ExternalPaymentGateway externalPaymentGateway;

    public PaymentGatewayAdapter(ExternalPaymentGateway externalPaymentGateway) {
        this.externalPaymentGateway = externalPaymentGateway;
    }

    @Override
    public PaymentResult process(PaymentRequest request) {
        //Translate the internal request to the external gateway call.
        ExternalPaymentResponse response = externalPaymentGateway.makeCardPayment(
                request.getPaymentMethod().name(),
                request.getCardNumber(),
                request.getAmount()
        );

        if (!response.isSuccessful()) {
            return PaymentResult.declined(response.getResponseMessage());
        }

        //Translate the external response back to the internal payment result model.
        return PaymentResult.approved(
                response.getExternalTransactionCode(),
                response.getExternalAuthorizationCode(),
                response.getResponseMessage()
        );
    }
}

package infrastructure.payment;

public class ExternalPaymentResponse {
    private final boolean successful;
    private final String externalTransactionCode;
    private final String externalAuthorizationCode;
    private final String responseMessage;

    public ExternalPaymentResponse(boolean successful, String externalTransactionCode, String externalAuthorizationCode, String responseMessage) {
        this.successful = successful;
        this.externalTransactionCode = externalTransactionCode;
        this.externalAuthorizationCode = externalAuthorizationCode;
        this.responseMessage = responseMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getExternalTransactionCode() {
        return externalTransactionCode;
    }

    public String getExternalAuthorizationCode() {
        return externalAuthorizationCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}

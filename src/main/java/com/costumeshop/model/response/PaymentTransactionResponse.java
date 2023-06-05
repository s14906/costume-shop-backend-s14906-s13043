package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentTransactionResponse extends AbstractResponse {
    private final Integer paymentTransactionId;

    @Builder
    public PaymentTransactionResponse(boolean success, String message, Integer paymentTransactionId) {
        super(success, message);
        this.paymentTransactionId = paymentTransactionId;
    }
}

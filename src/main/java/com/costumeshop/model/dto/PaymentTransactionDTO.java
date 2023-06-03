package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentTransactionDTO {
    private Integer userId;
    private Integer orderId;
    private BigDecimal paidAmount;
}

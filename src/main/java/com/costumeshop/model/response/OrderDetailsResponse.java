package com.costumeshop.model.response;

import com.costumeshop.model.dto.OrderDetailsDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDetailsResponse extends AbstractResponse {
    private final OrderDetailsDTO orderDetails;

    @Builder
    public OrderDetailsResponse(boolean success, String message, OrderDetailsDTO orderDetails) {
        super(success, message);
        this.orderDetails = orderDetails;
    }
}

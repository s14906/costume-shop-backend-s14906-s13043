package com.costumeshop.model.response;

import com.costumeshop.model.dto.OrderDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderResponse extends AbstractResponse {
    private final List<OrderDTO> orders;
    private final Integer orderId;

    @Builder
    public OrderResponse(boolean success, String message, List<OrderDTO> orders, Integer orderId) {
        super(success, message);
        this.orders = orders;
        this.orderId = orderId;
    }
}

package com.costumeshop.model.response;

import com.costumeshop.model.dto.OrderStatusDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderStatusResponse extends AbstractResponse {
    private final List<OrderStatusDTO> orderStatuses;

    @Builder
    public OrderStatusResponse(boolean success, String message, List<OrderStatusDTO> orderStatuses) {
        super(success, message);
        this.orderStatuses = orderStatuses;
    }
}

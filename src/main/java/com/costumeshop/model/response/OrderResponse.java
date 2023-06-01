package com.costumeshop.model.response;

import com.costumeshop.model.dto.OrderDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderResponse extends AbstractResponse {
    private final List<OrderDTO> orders;

    @Builder
    public OrderResponse(boolean success, String message, List<OrderDTO> orders) {
        super(success, message);
        this.orders = orders;
    }
}

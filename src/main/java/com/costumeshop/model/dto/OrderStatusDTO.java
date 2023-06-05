package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderStatusDTO {
    private String orderId;
    private Integer orderStatusId;
    private String status;
}

package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class OrderDetailsDTO {
    private Integer orderId;
    private Set<ItemWithImageDTO> items;

}

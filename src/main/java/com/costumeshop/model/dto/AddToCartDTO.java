package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddToCartDTO {
    private Integer userId;
    private Integer itemId;
    private Integer itemSizeId;
    private Integer itemAmount;
}

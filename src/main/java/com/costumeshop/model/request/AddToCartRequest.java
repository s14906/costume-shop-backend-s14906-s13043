package com.costumeshop.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private Integer userId;
    private Integer itemId;
    private Integer itemSizeId;
    private Integer itemAmount;
}

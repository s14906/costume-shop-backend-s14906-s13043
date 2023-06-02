package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemDTO {
    private Integer cartItemId;
    private String title;
    private Integer itemsAmount;
    private Double price;
    private String size;

}

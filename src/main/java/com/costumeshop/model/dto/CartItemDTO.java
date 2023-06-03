package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartItemDTO {
    private Integer cartItemId;
    private String title;
    private List<ItemDTO> items;
    private BigDecimal price;
    private String size;

}

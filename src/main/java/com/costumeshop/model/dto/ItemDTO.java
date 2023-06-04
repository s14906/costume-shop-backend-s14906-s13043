package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemDTO {
    private Integer itemId;
    private String title;
    private String description;
    private BigDecimal price;
    private List<ItemImageDTO> itemImages;
    private Integer quantity;
    private Integer visible;
    private String itemCategory;
    private String itemSet;
}

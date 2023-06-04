package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemCategoryDTO {
    private Integer itemCategoryId;
    private String category;
}

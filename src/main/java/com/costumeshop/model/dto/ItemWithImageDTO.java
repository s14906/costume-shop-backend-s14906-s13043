package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemWithImageDTO {
    private Integer itemId;
    private String title;
    private String description;
    private Double price;
    private List<ItemImageDTO> itemImages;

}

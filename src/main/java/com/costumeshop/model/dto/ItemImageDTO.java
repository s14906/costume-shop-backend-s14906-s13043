package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemImageDTO {
    private Integer imageId;
    private String imageBase64;
}

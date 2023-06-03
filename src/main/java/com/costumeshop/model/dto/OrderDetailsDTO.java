package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class OrderDetailsDTO {
    private Date orderDate;
    private Integer orderId;
    private ComplaintDTO complaint;
    private Integer buyerId;
    private Set<ItemDTO> items;

}

package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class OrderDTO {
    private Integer orderId;
    private UserDTO user;
    private String orderStatus;
    private Date createdDate;
    private Double totalPrice;

}

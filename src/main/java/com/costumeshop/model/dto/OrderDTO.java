package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDTO {
    private Integer orderId;
    private UserDTO user;
    private String orderStatus;
    private Date createdDate;
    private BigDecimal totalPrice;
    private AddressDTO address;
    private List<OrderDetailsDTO> ordersDetails;

}

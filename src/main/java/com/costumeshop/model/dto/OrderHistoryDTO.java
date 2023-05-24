package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class OrderHistoryDTO {
    private Integer orderId;
    private String orderUserName;
    private String orderUserSurname;
    private String orderStatus;
    private Date createdDate;

}

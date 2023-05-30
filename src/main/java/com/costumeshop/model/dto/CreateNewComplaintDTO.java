package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateNewComplaintDTO {
    private Integer userId;
    private Integer orderId;
    private String complaintCategory;
    private String complaintMessage;


}

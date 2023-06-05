package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ComplaintDTO {
    private Integer complaintId;
    private Integer buyerId;
    private Integer employeeId;
    private String buyerName;
    private String buyerSurname;
    private String employeeName;
    private String employeeSurname;
    private String complaintStatus;
    private Integer orderId;
    private Date createdDate;
}

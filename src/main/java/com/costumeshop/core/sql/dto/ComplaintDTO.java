package com.costumeshop.core.sql.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

}

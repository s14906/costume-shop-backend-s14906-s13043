package com.costumeshop.model.response;

import com.costumeshop.model.dto.ComplaintDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ComplaintResponse extends AbstractResponse {
    private final Integer complaintId;
    private final List<ComplaintDTO> complaints;

    @Builder
    public ComplaintResponse(boolean success, String message, Integer complaintId, List<ComplaintDTO> complaints) {
        super(success, message);
        this.complaintId = complaintId;
        this.complaints = complaints;
    }
}

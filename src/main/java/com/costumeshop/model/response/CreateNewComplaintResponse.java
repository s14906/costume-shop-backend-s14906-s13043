package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateNewComplaintResponse extends AbstractResponse {
    private final Integer complaintId;

    @Builder
    public CreateNewComplaintResponse(boolean success, String message, Integer complaintId) {
        super(success, message);
        this.complaintId = complaintId;
    }
}

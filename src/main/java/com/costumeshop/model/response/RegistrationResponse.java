package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RegistrationResponse extends AbstractResponse {

    @Builder
    public RegistrationResponse(boolean success, String message) {
        super(success, message);
    }
}

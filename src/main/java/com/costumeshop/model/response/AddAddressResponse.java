package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddAddressResponse extends AbstractResponse {
    @Builder
    public AddAddressResponse(boolean success, String message) {
        super(success, message);
    }
}

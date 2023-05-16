package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddToCartResponse extends AbstractResponse {

    @Builder
    public AddToCartResponse(boolean success, String message) {
        super(success, message);
    }
}

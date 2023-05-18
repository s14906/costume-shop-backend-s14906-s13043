package com.costumeshop.model.response;

import lombok.Builder;

public class SimpleResponse extends AbstractResponse {
    @Builder
    public SimpleResponse(boolean success, String message) {
        super(success, message);
    }
}

package com.costumeshop.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AbstractResponse {
    boolean success;
    String message;
}

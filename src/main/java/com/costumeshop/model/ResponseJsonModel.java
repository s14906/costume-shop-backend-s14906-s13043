package com.costumeshop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseJsonModel {
    private boolean success;
    private String message;

    private Object body;
}

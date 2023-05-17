package com.costumeshop.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAddressRequest {
    private Integer userId;
    private String street;
    private String postalCode;
    private String city;
    private String flatNumber;
}
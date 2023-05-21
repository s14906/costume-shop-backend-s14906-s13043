package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressDTO {
    private Integer userId;
    private Integer addressId;
    private String street;
    private String postalCode;
    private String city;
    private String flatNumber;
}
package com.costumeshop.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String email;
    private String password;
    private String username;
    private String name;
    private String surname;
    private String street;
    private String postalCode;
    private String city;
    private String flatNumber;
    private String phone;
}

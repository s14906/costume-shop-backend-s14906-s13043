package com.costumeshop.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String username;
    private String name;
    private String surname;
    private String phone;
    private AddressDTO address;
}

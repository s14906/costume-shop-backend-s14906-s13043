package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserDTO {
    private Integer id;
    private String email;
    private String password;
    private String username;
    private Integer emailVerified;
    private String name;
    private String surname;
    private String token;
    private String phone;
    private List<String> roles;

}

package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class UserLoginResponse extends AbstractResponse {
    private final String token;
    private final Long id;
    private final String username;
    private final String name;
    private final String surname;
    private final String phone;
    private final String email;
    private final List<String> roles;

    @Builder
    public UserLoginResponse(boolean success, String message, String token, Long id, String username, String name, String surname, String phone, String email, List<String> roles) {
        super(success, message);
        this.token = token;
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.roles = roles;
    }
}

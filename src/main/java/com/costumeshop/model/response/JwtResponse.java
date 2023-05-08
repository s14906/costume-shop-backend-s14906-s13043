package com.costumeshop.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class JwtResponse extends AbstractResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    @Builder
    public JwtResponse(boolean success, String message, String token, Long id, String username, String email, List<String> roles) {
        super(success, message);
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}

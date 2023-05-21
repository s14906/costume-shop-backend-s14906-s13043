package com.costumeshop.model.response;

import com.costumeshop.model.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse extends AbstractResponse {
    private final UserDTO user;

    @Builder
    public UserResponse(boolean success, String message, UserDTO user) {
        super(success, message);
        this.user = user;
    }
}

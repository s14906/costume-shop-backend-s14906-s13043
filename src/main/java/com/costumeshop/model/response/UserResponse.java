package com.costumeshop.model.response;

import com.costumeshop.core.sql.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse extends AbstractResponse {
    private final User user;

    @Builder
    public UserResponse(boolean success, String message, User user) {
        super(success, message);
        this.user = user;
    }
}

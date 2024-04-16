package com.costumeshop.data;

import com.costumeshop.core.sql.entity.User;

import java.util.Collections;

public class GivenUser {

    public User withTestUsernameAndEmptyUserRoles() {
        User user = new User();
        user.setUsername("testUsername");
        user.setUserRoles(Collections.emptySet());
        return user;
    }
}

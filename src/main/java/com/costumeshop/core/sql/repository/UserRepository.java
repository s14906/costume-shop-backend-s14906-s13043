package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findByEmail(String email);

    User findByUsername(String username);

    User findByVerificationToken(String verificationToken);

}

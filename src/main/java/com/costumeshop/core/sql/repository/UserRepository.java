package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1")

    User findByUsername(String username);
}

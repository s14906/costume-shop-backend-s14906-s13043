package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {
}

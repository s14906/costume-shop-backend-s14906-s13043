package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Integer> {
}

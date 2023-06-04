package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ItemSet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemSetRepository extends CrudRepository<ItemSet, Integer> {
    Optional<ItemSet> findBySet(String set);
}

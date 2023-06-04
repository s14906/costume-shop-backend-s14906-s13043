package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ItemCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends CrudRepository<ItemCategory, Integer>  {
    Optional<ItemCategory> findByCategory(String category);
}

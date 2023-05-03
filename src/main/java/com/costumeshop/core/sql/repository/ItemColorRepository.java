package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ItemColor;
import com.costumeshop.core.sql.entity.ItemSize;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemColorRepository extends CrudRepository<ItemColor, Integer> {
}
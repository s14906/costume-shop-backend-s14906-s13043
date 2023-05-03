package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.Item;
import com.costumeshop.core.sql.entity.ItemSize;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemSizeRepository extends CrudRepository<ItemSize, Integer> {
}

package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ItemImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends CrudRepository<ItemImage, Integer> {
}

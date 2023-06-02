package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
    @Query("SELECT i FROM Item i WHERE i.title LIKE %:searchText% OR i.description LIKE %:searchText%")
    List<Item> findAllByTitleOrDescription(@Param("searchText") String searchText);

}

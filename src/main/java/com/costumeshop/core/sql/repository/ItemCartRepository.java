package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.Item;
import com.costumeshop.core.sql.entity.ItemCart;
import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.core.sql.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCartRepository extends CrudRepository<ItemCart, Integer> {
    List<ItemCart> findAllByUserId(Integer userId);
    List<ItemCart> findAllByUserAndItemAndItemSize(User user, Item item, ItemSize itemSize);
    ItemCart findByUserIdAndItemId(Integer userId, Integer cartItemId);
}

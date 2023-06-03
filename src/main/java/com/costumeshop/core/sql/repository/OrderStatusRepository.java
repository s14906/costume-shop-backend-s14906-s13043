package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.OrderStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends CrudRepository<OrderStatus, Integer>  {
    OrderStatus findByStatus(String status);
}

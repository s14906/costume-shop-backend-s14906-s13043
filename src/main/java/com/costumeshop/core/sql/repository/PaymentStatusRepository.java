package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.PaymentStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends CrudRepository<PaymentStatus, Integer>  {
    Optional<PaymentStatus> findByStatus(String status);
}

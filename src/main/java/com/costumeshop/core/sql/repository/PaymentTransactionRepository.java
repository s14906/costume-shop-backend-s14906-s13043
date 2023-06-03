package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.PaymentTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends CrudRepository<PaymentTransaction, Integer> {
}

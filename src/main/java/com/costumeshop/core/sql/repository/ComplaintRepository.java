package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.Complaint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends CrudRepository<Complaint, Integer> {

}

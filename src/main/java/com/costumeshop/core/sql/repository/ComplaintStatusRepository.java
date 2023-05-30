package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ComplaintStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintStatusRepository extends CrudRepository<ComplaintStatus, Integer> {
    ComplaintStatus findComplaintStatusByStatus(String status);

}

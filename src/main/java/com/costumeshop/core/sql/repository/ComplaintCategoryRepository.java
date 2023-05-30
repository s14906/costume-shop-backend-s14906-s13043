package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ComplaintCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintCategoryRepository extends CrudRepository<ComplaintCategory, Integer> {
    ComplaintCategory findComplaintCategoryByCategory(String category);
}

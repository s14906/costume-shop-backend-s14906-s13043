package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ComplaintChatImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintChatImageRepository extends CrudRepository<ComplaintChatImage, Integer> {
}

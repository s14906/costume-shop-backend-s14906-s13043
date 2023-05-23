package com.costumeshop.core.sql.repository;

import com.costumeshop.core.sql.entity.ComplaintChatMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintChatMessageRepository extends CrudRepository<ComplaintChatMessage, Integer> {
}

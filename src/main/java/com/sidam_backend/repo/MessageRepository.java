package com.sidam_backend.repo;

import com.sidam_backend.data.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}

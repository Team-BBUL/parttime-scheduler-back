package com.sidam_backend.repo;

import com.sidam_backend.data.UserChatting;
import org.springframework.data.repository.CrudRepository;

public interface UserChattingRepository extends CrudRepository<UserChatting, Long> {
}

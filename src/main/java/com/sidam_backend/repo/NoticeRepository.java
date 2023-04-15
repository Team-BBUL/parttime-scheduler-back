package com.sidam_backend.repo;

import com.sidam_backend.data.Notice;
import org.springframework.data.repository.CrudRepository;

public interface NoticeRepository extends CrudRepository<Notice, Long> {
}

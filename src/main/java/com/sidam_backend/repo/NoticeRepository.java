package com.sidam_backend.repo;

import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoticeRepository extends CrudRepository<Notice, Long> {

    List<Notice> findAllByStore (Store store);
}

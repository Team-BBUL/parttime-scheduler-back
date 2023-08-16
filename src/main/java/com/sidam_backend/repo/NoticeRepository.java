package com.sidam_backend.repo;

import com.sidam_backend.data.Notice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends CrudRepository<Notice, Long> {

    @Query(value="SELECT * FROM notice_tbl WHERE store_id = :storeId AND id < :last AND valid = true ORDER BY id DESC LIMIT :cnt", nativeQuery = true)
    List<Notice> selectAllAfterLast(int last, Long storeId, int cnt);

    @Query(value="SELECT id FROM notice_tbl WHERE store_id = :storeId AND valid = true ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Long> selectLastId(Long storeId);
}

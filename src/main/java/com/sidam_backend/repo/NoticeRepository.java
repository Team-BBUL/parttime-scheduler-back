package com.sidam_backend.repo;

import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoticeRepository extends CrudRepository<Notice, Long> {

    List<Notice> findAllByStore (Store store);

    @Query(value="SELECT * FROM notice WHERE store = ?2 AND id < ?1 LiMIT ?3", nativeQuery = true)
    List<Notice> selectAllAfterLast(int last, Store store, int cnt);
}

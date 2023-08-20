package com.sidam_backend.repo;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Notice;
import com.sidam_backend.data.NoticeReceive;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NoticeReceiveRepository extends CrudRepository<NoticeReceive, Long> {

    Optional<NoticeReceive> findByNoticeAndRole(Notice notice, AccountRole role);

    @Query("select nr.check from NoticeReceive nr where nr.notice = :notice and nr.role = :role ")
    Optional<Boolean> findCheckByNoticeAndRole(Notice notice, AccountRole role);
}
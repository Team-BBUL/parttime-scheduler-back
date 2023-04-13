package com.sidam_backend.repo;

import com.sidam_backend.data.Announcement;
import org.springframework.data.repository.CrudRepository;

public interface AnnouncementRepository extends CrudRepository<Announcement, Long> {
}

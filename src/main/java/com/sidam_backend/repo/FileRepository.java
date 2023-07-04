package com.sidam_backend.repo;

import com.sidam_backend.data.ImageFile;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<ImageFile, Long> {
}

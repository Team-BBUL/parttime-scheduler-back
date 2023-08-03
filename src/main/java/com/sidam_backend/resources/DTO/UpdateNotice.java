package com.sidam_backend.resources.DTO;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateNotice {

    private Long id;
    private String subject;
    private String content;
    private List<MultipartFile> photo;

}

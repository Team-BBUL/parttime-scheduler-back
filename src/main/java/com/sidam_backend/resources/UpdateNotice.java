package com.sidam_backend.resources;

import com.sidam_backend.data.Notice;
import lombok.Data;

import java.util.List;

@Data
public class UpdateNotice {

    private Long id;
    private String subject;
    private String content;
    private List<UploadFile> photo;

}

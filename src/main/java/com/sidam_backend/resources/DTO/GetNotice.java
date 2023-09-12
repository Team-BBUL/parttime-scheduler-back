package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetNotice {

    private Long id;
    private String subject;
    private String content;
    private List<GetImage> photo;
    private LocalDateTime timeStamp;
}

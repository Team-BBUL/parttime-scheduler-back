package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNoticeList {

    private Long id;
    private String subject;
    private boolean check;
    private LocalDateTime timeStamp;
}

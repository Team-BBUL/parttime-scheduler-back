package com.sidam_backend.resources;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNoticeList {

    private Long id;
    private String subject;
    private LocalDateTime date;
}

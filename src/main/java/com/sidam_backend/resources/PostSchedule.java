package com.sidam_backend.resources;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostSchedule {

    private LocalDateTime timeStamp;
    private List<PostDaily> date;
}

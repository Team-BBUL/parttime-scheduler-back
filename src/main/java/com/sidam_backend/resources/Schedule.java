package com.sidam_backend.resources;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Schedule {

    private LocalDateTime timeStamp;
    private List<Daily> date;
}

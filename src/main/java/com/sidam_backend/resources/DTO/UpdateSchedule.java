package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateSchedule {

    private LocalDateTime timeStamp;
    private List<GetDaily> date;
}

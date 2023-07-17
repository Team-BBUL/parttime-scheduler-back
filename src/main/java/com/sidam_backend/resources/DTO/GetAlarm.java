package com.sidam_backend.resources.DTO;

import com.sidam_backend.data.Alarm;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAlarm {

    private long id;
    private Alarm.Category type;
    private String content;
    private boolean read;
    private LocalDateTime date;
    private Long request;
}

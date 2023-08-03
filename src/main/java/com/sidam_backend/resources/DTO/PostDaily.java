package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PostDaily {

    private LocalDate day;
    private List<Boolean> time;
    private List<Long> workers;
}

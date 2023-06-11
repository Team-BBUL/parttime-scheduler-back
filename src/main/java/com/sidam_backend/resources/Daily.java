package com.sidam_backend.resources;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Daily {

    private LocalDate day;
    private List<Boolean> time;
    private List<Long> workers;
}

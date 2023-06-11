package com.sidam_backend.resources;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostDaily {

    private Long id;
    private LocalDate day;
    private List<Boolean> time = new ArrayList<>();
    private List<Worker> workers;
}

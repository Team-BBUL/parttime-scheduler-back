package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetDaily {

    private Long id;
    private LocalDate day;
    private List<Boolean> time = new ArrayList<>();
    private List<Worker> workers;
}

package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ImpossibleTime {

    private Long id;
    private LocalDate date;
    private List<Boolean> time;
}

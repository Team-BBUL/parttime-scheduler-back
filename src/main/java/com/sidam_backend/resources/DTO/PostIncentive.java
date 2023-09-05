package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostIncentive {

    private int cost;

    private String description;

    private LocalDate date;
}

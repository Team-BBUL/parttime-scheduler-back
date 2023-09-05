package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostPolicy {

    private int multiplyCost;

    private String description;

    private LocalDate date;
}

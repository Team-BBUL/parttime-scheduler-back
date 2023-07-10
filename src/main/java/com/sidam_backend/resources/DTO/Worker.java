package com.sidam_backend.resources.DTO;

import lombok.Data;

@Data
public class Worker {
    // users : id, alias, color, cost
    private Long id;
    private String alias;
    private String color;
    private int cost;
}

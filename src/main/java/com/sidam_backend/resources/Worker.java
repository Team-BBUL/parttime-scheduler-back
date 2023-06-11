package com.sidam_backend.resources;

import lombok.Data;

@Data
public class Worker {
    // users : id, alias, color, cost
    private Long id;
    private String alias;
    private String color;
    private int cost;
}

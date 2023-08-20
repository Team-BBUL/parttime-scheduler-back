package com.sidam_backend.resources.DTO;

import lombok.Data;

@Data
public class Worker {

    public Worker() {}
    public Worker(long id, String alias, String color, int cost) {
        this.id = id;
        this.alias = alias;
        this.cost = cost;
        this.color = color;
    }
    // users : id, alias, color, cost
    private Long id;
    private String alias;
    private String color;
    private int cost;
}

package com.sidam_backend.resources.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostAnniversary {

    private String name;

    private LocalDateTime date;
}

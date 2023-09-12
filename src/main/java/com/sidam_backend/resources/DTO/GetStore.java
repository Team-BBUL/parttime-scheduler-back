package com.sidam_backend.resources.DTO;

import lombok.Data;

@Data
public class GetStore {

    private Long id;
    private String name;
    private String location;
    private String phone;
}

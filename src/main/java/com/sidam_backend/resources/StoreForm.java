package com.sidam_backend.resources;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StoreForm {

    @NotBlank
    private String name;

    private String location;

    private String phone;

    private int open;

    private int closed;

    private int payday;

    private int weekStartDay;

}

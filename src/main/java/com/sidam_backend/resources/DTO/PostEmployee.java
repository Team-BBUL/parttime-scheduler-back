package com.sidam_backend.resources.DTO;

import com.sidam_backend.data.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PostEmployee {

    @NotBlank
    private String accountId;

    @NotBlank
    private String password;

    @NotBlank
    private String alias;

    @NotNull
    private int level = 0;

    @Positive
    private int cost;

    @NotBlank
    private String color = "white";

    @NotNull
    private boolean isSalary;

    @Enumerated(EnumType.STRING)
    private Role role;
}

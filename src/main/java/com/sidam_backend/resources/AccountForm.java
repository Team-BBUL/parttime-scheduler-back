package com.sidam_backend.resources;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountForm {
    @NotBlank
    private String name;

    private boolean onceVerified;
}

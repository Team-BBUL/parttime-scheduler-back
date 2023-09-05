package com.sidam_backend.resources.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountForm {
    @NotBlank
    private String name;
}

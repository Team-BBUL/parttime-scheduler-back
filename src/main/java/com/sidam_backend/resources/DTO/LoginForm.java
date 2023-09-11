package com.sidam_backend.resources.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginForm {

    @NotBlank
    @Length(min = 3, max = 20)
    private String accountId;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}

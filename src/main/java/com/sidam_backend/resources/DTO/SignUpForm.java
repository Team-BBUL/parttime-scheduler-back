package com.sidam_backend.resources.DTO;

import com.sidam_backend.data.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3, max = 20)
    private String accountId;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    @NotNull
    private Role role;
}

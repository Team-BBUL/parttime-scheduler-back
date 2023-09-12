package com.sidam_backend.security;

import com.sidam_backend.data.enums.Role;
import lombok.Data;

@Data
public class AccountDetail {

    private Long id;

    private String accountId;

    private Role role;
}

package com.sidam_backend.security;

import com.sidam_backend.data.enums.Role;
import lombok.Data;

@Data
public class AccountDetail {

    public Long id;

    public String accountId;

    public Role role;
}

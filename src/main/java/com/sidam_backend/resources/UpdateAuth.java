package com.sidam_backend.resources;

import lombok.Data;

@Data
public class UpdateAuth {

    private String accountId;

    private String password;

    private String checkPassword;
}

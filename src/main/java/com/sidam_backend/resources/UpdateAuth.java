package com.sidam_backend.resources;

import lombok.Data;

@Data
public class UpdateAuth {

    private String accountId; // 바꿀 ID

    private String password; // 바꿀 비밀번호

    private String checkPassword; // 바꾼 거 확인 비밀번호
}

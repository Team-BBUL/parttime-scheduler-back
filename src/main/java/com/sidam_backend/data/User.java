package com.sidam_backend.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name="UserTbl")
@Builder @AllArgsConstructor @NoArgsConstructor
public class User implements Serializable {

    @Id
    private String kakaoId;

    @NotBlank
    private String profile;

    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    private String device;
}

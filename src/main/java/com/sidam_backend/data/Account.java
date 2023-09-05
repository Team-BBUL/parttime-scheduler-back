package com.sidam_backend.data;

import com.sidam_backend.data.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;



@Data
@Entity
@Table(name="account")
public class Account implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String email;

//    @NotBlank
//    private String profile;

    @NotBlank
    private String name;

//    @NotBlank
    private String device;

    @NotBlank
    private String oauth2Id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    private boolean onceVerified;

    public Object update(String name) {
        this.name = name;
        return this;
    }

    public String getRoleKey() {
        return role.name();
    }
}
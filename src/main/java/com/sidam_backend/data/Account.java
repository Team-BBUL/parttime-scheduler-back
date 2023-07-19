package com.sidam_backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sidam_backend.data.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private String profile;

//    @NotBlank
    private String name;

//    @NotBlank
    private String phone;

//    @NotBlank
    private String device;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Object update(String name) {
        this.name = name;
        return this;
    }

    public String getRoleKey() {
        return role.name();
    }
}
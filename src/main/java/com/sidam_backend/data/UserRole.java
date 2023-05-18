package com.sidam_backend.data;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name="user_role")
public class UserRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String alias;

    @NotNull
    private int level;

    @Positive
    private int cost;

    @NotBlank
    private String color;

    @NotNull
    private boolean isSalary;

    @NotNull
    private boolean valid;

    @ManyToOne
    private User user;

    @ManyToOne
    private Store store;
}

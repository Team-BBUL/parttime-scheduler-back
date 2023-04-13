package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="store")
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long storeId;

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String phone;

    @NotBlank
    private String open;
    // hh:mm 형식

    @NotBlank
    private String close;
    // hh:mm 형식
}

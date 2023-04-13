package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="anniversary")
public class Anniversary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long anniversaryId;

    @NotBlank
    private String name;

    @NotBlank
    private String date;
    // MM-dd ??

    @ManyToOne
    private UserRole userRoleId;
}

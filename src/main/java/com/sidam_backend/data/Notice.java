package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="notice")
public class Notice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    private Date date = new Date();

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;
}

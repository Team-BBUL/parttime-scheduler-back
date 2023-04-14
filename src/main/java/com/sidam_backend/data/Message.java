package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String content;

    @NotBlank
    private Date date = new Date();
    // MM-dd

    @ManyToOne
    private ChattingRoom chattingRoom;

    @ManyToOne
    private UserRole userRole;
}

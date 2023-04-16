package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

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
    private LocalDateTime date = LocalDateTime.now();
    // MM-dd

    @ManyToOne
    private ChattingRoom chattingRoom;

    @ManyToOne
    private UserRole userRole;
}

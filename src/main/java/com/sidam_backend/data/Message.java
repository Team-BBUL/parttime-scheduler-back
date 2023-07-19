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
    @JoinColumn(name="room_id")
    private ChattingRoom chattingRoom;

    @ManyToOne
    @JoinColumn(name="role_id")
    private AccountRole userRole;
}

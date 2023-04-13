package com.sidam_backend.data;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="user_chatting")
public class UserChatting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userChattingId;

    @ManyToOne
    private UserRole userRoleId;

    @ManyToOne
    private ChattingRoom chattingId;
}

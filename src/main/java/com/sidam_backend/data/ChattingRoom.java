package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="user_chatting")
public class ChattingRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long chattingId;

    @NotBlank
    private String createDate;

    @ManyToOne
    private Store storeId;
}

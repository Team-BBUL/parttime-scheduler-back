package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table
public class NoticeReceive {

    public NoticeReceive() {}
    public NoticeReceive(Notice notice, AccountRole role) {
        this.notice = notice;
        this.role = role;
        check = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private AccountRole role;

    @NotNull
    @Column(name = "check_bit")
    private boolean check;
}
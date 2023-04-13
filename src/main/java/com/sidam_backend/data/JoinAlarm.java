package com.sidam_backend.data;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="join_alarm")
public class JoinAlarm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long joinId;

    private Date date = new Date();

    @OneToOne
    private Store storeId;

    @OneToOne
    private UserRole userRoleId;
}

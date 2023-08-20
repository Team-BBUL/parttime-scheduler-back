package com.sidam_backend.data;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.resources.DTO.Worker;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name="account_role")
public class AccountRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String alias;

    @NotNull
    private int level;

    @Positive
    private int cost;

    @NotBlank
    private String color;

    @NotNull
    private boolean isSalary = true;

    @NotNull
    private boolean valid;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "kakao_id")
    @JsonIgnore
    private Account account;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnore
    private Store store;

    public boolean getSalary() {
        return isSalary;
    }

    // role은 요청을 보낸 유저 => 근무자의 경우 본인이 아닌 사람의 cost를 0으로 setting하기 위함
    public Worker toWorker(AccountRole role) {

        Worker worker = new Worker();

        worker.setId(id);
        worker.setAlias(alias);
        worker.setColor(color);

        if (!role.isSalary || Objects.equals(role.id, id)) {
            worker.setCost(cost);
        } else {
            worker.setCost(0);
        }

        return worker;
    }

    public void isValidEmail(String email){
        if (!this.getAccount().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인이 아닙니다.");
        }
    }

    public boolean isManager(){
        return this.role == Role.MANAGER;
    }

    public Worker toWorker() {

        return new Worker(id, alias, color, 0);
    }
}

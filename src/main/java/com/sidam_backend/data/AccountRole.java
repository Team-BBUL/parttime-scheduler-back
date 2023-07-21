package com.sidam_backend.data;

import java.io.Serializable;
import java.util.Objects;

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

    @ManyToOne
    @JoinColumn(name="kakao_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name="store_id")
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
}

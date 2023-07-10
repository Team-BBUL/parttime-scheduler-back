package com.sidam_backend.data;

import java.io.Serializable;
import java.util.Objects;

import com.sidam_backend.resources.DTO.Worker;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name="user_role")
public class UserRole implements Serializable {

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
    private User member;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    public boolean getSalary() {
        return isSalary;
    }

    public Worker toWorker(UserRole role) {

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

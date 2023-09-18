package com.sidam_backend.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.resources.DTO.Worker;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@Entity
@Table(name="account_role")
@NamedEntityGraph(name = "AccountRole.store", attributeNodes = @NamedAttributeNode("store"))
public class AccountRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String accountId;

    @NotBlank
    private String password;

    @NotBlank
    @Column(unique = true)
    private String originAccountId;

    @NotBlank
    private String originPassword;

//    @NotBlank
    private String alias;

    @NotNull
    private int level = 0;

//    @Positive
    private int cost;

    @NotBlank
    private String color = "0xFFFFFFFF";

    @NotNull
    private boolean isSalary = true;

    @NotNull
    private boolean valid = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime joinedAt;

    public void completeSignUp() {
        this.valid = true;
        this.joinedAt = LocalDateTime.now();
    }
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

    public boolean isManager(){
        return this.role == Role.MANAGER;
    }

    public boolean isSame(Long id){
        return this.getId().equals(id);
    }

    public Worker toWorker() {

        return new Worker(id, alias, color, 0);
    }
}

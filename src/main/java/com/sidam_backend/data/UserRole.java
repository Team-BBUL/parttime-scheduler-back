package com.sidam_backend.data;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="user_role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRole implements Serializable {

    @Id
    private String userRoleId;
    // 고유 아이디 생성 규칙이 있으면 좋을 것 같은데
    // ex) 0000-0000-0000 (매장별고유값-점주/직원여부-고유번호)

    @NotBlank
    private String alias;

    @NotNull
    private int level;

    @Positive
    private int cost;

    @NotBlank
    private String color;

    @NotNull
    private boolean isSalary;

    @NotNull
    private boolean valid;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User userKakaoId;
}

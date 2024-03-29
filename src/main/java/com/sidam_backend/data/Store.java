package com.sidam_backend.data;

import com.sidam_backend.data.enums.Role;
import com.sidam_backend.resources.DTO.GetStore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name="store")
public class Store implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

//    @NotBlank
    private String location;

//    @NotBlank
    private String phone;

//    @NotBlank
    private int open;
    // hh 형식

//    @NotBlank
    private int closed;
    // hh 형식

    @NotNull
    private int idx;

//    @NotNull
    private int payday;
    // 주차 시작일 = 월 화 수 목 금 토 일
    //             1  2  3 4  5  6 7
//    @NotNull
    private int startDayOfWeek;

    // 근무불가능시간 선택 마감일 = 주차 시작일로부터 n일 전까지
//    @NotNull
    private int deadlineOfSubmit;

    public GetStore toGetStore() {
        GetStore store = new GetStore();

        store.setId(id);
        store.setName(name);
        store.setLocation(location);
        store.setPhone(phone);
        return store;
    }
}

package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name="store")
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String phone;

    @NotBlank
    private int open;
    // hh 형식

    @NotBlank
    private int close;
    // hh 형식

    @NotNull
    private int idx;

    // 근무불가능시간 선택 마감일 = 주차 시작일로부터 n일 전까지
    @NotNull
    private int deadline;

    // 주차 시작일 = 월 화 수 목 금 토 일
    //             1  2  3 4  5  6 7
    @NotNull
    private int week;
}

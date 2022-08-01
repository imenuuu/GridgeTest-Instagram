package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private Long userId;
    private String userLoginId;
    private String phoneNumber;
    private Date logInDate;
}

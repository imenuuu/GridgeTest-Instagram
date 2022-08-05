package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private Long id;
    private String userId;
    private String profileImg;
    private Date birth;
    private String password;
    private String name;
    private String webSite;
    private String introduce;
    private String phoneNumber;
    private String userReact;
    private String userStatus;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Timestamp logInDate;
    private String userPublic;
    private String agreeInfo;
    private String suspensionStatus;
}

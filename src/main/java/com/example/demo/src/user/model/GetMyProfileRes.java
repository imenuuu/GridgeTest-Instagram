package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMyProfileRes {
    private Long userId;
    private String userName;
    private String profileImgUrl;
    private String name;
    private String introduce;
    private String website;
    private int boardCnt;
    private int followerCnt;
    private int followingCNt;
    private List<GetProfileBoardRes> getProfileBoardRes;
}

package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserProfileRes {
    private String userPublic;
    private Long userId;
    private Long chatId;
    private String userLoginId;
    private String profileImgUrl;
    private String name;
    private String introduce;
    private String website;
    private int boardCnt;
    private int followerCnt;
    private int followingCNt;
    private int followCheck;
    private List<GetProfileBoardRes> getProfileBoard;
}

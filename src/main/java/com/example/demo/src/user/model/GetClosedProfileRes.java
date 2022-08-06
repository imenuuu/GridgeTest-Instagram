package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetClosedProfileRes {
    private Long userId;
    private Long chatId;
    private String userNickname;
    private String profileImgUrl;
    private String name;
    private String introduce;
    private String website;
    private int boardCnt;
    private int followerCnt;
    private int followingCnt;
    private int reqFollowCheck;
}

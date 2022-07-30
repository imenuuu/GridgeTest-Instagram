package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetFollowKeepRes {
    private Long requestId;
    private Long userId;
    private String profileImgUrl;
    private String userLoginId;
    private String userName;

}

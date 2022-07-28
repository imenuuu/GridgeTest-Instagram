package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor

public class PostKakaoUserReq {
    @ApiModelProperty(example="카카오 액세스 토큰")
    private String accessToken;
    @ApiModelProperty(example="전화번호")
    private String phoneNumber;
    @ApiModelProperty(example="성함")
    private String name;
    @ApiModelProperty(example="생일")
    private Date birth;
    @ApiModelProperty(example="아이디")
    private String userId;
}

package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    @ApiModelProperty(example="전화번호")
    private String phoneNumber;
    @ApiModelProperty(example="성함")
    private String name;
    @ApiModelProperty(example="비밀 번호")
    private String password;
    @ApiModelProperty(example="생일")
    private Date birth;
    @ApiModelProperty(example="아이디")
    private String userId;
}

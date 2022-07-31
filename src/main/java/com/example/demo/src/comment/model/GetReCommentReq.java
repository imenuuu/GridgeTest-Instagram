package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetReCommentReq {
    private Long userId;
    private String profileImgUrl;
    private String userLoginId;
    private Long reCommentId;
    private String reComment;
    private String reCommentTime;
    private int likeCnt;
    private int likeCheck;
}

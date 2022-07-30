package com.example.demo.src.comment.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private Long userId;
    private String profileImgUrl;
    private String userLoginId;
    private Long commentId;
    private String comment;
    private String commentTime;
    private int likeCnt;
    private int reCommentCnt;
    private int likeCheck;

}

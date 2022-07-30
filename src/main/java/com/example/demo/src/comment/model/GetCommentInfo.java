package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentInfo {
    private Long userId;
    private String profileImgUrl;
    private String userLoginId;
    private String description;
    private String boardTime;
    private List<GetCommentRes> commentList;
}

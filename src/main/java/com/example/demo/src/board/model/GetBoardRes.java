package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetBoardRes {
    private Long userId;
    private String profileImgUrl;
    private String userLoginId;
    private Long boardId;
    private String description;
    private int likeCheck;
    private int likeCnt;
    private int commentCnt;
    private String boardTime;
    private List<GetBoardImgRes> boardImgList;
}

package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardRes {
    private String profileImgUrl;
    private Long userId;
    private String userName;
    private Long boardId;
    private String description;
    private int likeCheck;
    private int likeCnt;
    private int commentCnt;
    private String boardTime;
    private List<GetBoardImgRes> getBoardImgRes;
}

package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostBoardReq {
    private List<BoardImg> boardImg;
    private Long userId;
    private String description;
}

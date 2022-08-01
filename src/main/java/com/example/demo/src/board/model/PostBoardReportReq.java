package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostBoardReportReq {
    private Long userId;
    private Long boardId;
    private Long reportId;
}

package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class GetBoardRes {
    private Long boardId;
    private String userLoginId;
    private String description;
    private String boardDate;
}

package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardInfoRes {
    private Long boardId;
    private Long userId;
    private String description;
    private Date createdDate;
    private Date updatedDate;
    private String suspensionStatus;
    private String status;
    private String dropStatus;
    private List<BoardImg> boardImgList;
}

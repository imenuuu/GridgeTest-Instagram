package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardReportRes {
    private Long reportId;
    private String userName;
    private Long boardId;
    private String description;
    private String cause;
    private Timestamp createdDate;
}

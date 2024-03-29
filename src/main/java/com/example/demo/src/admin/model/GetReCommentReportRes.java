package com.example.demo.src.admin.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetReCommentReportRes {
    private Long reportId;
    private String userName;
    private Long reCommentId;
    private String reComment;
    private String cause;
    private Timestamp createdDate;
}

package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReCommentReportReq {
    private Long userId;
    private Long ReCommentId;
    private Long reportId;

}

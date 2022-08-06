package com.example.demo.src.comment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentReportReq {
    @JsonProperty("userId")
    private Long userId;
    private Long commentId;
    private Long reportId;
}

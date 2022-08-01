package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserReq {
    private String nameQuery;
    private String userIdQuery;
    private String statusQuery;
    private String dateQuery;
    private int paging;
}

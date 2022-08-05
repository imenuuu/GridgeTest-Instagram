package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLogQueryReq {
    private String typeQuery;
    private String dateQuery;
    private int paging;
}

package com.example.demo.src.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetLogRes {
    private Long logId;
    private String type;
    private String userLoginId;
    private Timestamp logCreated;
}

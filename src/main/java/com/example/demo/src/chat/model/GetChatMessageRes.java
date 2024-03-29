package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatMessageRes {
    private Long userId;
    private String profileImgUrl;
    private Long messageId;
    private String message;
    private String messageTime;
    private int likeCheck;
}

package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatMessageRes {
    private String type;  // 메세지 타입 : 입장, 채팅, 나가기
    private Long chatId;
    private Long userId;
    private String userLoginId;
    private String userName;
    private Long messageId;
    private String message;
}

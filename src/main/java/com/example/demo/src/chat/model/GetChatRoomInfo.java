package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetChatRoomInfo {
    private Long chatId;
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String userLoginId;
    private List<GetChatMessageRes> messageList;
}

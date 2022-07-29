package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetChatIdRes {
    private Long chatId;
    private List<GetChatRoomRes> getChatRoomList;
}

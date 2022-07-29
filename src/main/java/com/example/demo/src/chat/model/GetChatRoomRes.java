package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomRes {
    private String profileImgUrl;
    private String userName;
    private String lastMessage;
    private String lastMessageTime;
}
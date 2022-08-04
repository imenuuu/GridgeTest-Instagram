package com.example.demo.src.chat;

import com.example.demo.src.comment.CommentDao;
import org.springframework.stereotype.Service;

@Service
public class ChatProvider {
    private final ChatDao chatDao;

    public ChatProvider(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

}

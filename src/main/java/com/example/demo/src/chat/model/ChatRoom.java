package com.example.demo.src.chat.model;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.ChatService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChatRoom {
    private Long roomId;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(Long roomId, String name){
        this.roomId = roomId;
    }

    // 채팅 서비스

    /*
    채팅방 입장, 대화, 퇴장은 Socket 통신
    * Request 예시
        {
          "type":"ENTER",
          "chatId":1,
          "userId":"user123",
          "user_name":"chulsu",
          "message":""
        }
    */

    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService) throws BaseException {
        // chatMessage 파싱
        String type= chatMessage.getType();
        Long chatId = chatMessage.getChatId();
        Long userId = chatMessage.getUserId();
        String message= chatMessage.getMessage();

        // 채팅방 입장
        if(type.equals("ENTER")) {
            // 세션 생성 및 채팅방 메시지 불러오기
            sessions.add(session);


            if (chatService.checkUserIn(chatId).equals("FALSE")) {
                chatService.addChatMember(chatId);
            }
        }

        // 채팅방 퇴장
        else if(type.equals("OUT")){
            // 세션 삭제
            sessions.remove(session);
            // 채팅방 삭제 = true, 멤버만 삭제 = false
        }

        // 뒤로가기
       else if(type.equals("BACK")){
            // 세션 삭제
            sessions.remove(session);
            return ; // 뒤로 갔다는 메시지를 기록하지 않고 보내지도 않음
        }


       // chat룸 상태 TRUE 로 변경
        // DB에 메시지 기록
        else if(type.equals("SEND")) {
            chatService.addChatMessage(chatId, userId, message);
            // 모든 사용자에게 메시지 보내기
            sendMessage(chatMessage, chatService);
        }
    }
    
    // 메시지 전송
    public <T> void sendMessage(T message, ChatService chatService){
        sessions.parallelStream().forEach(session -> chatService.sendMessage(session,message));
    }
}
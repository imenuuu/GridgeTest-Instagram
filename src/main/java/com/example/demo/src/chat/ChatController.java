package com.example.demo.src.chat;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    List<GetChatIdRes> chatId;
    private final ChatService chatService;

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

    /*
    채팅방 생성
    * Request 예시
        {
            "chat_name":"chatroom",
            "chat_restaurant":"chicken",
            "userId":[
                {"id":"user123"},
                {"id":"user124"},
                {"id":"user125"}
            ]
        }
    */

    // 채팅방 생성
    @ResponseBody
    @PostMapping("/create")
    public BaseResponse<String> createChat(@RequestBody PostChatReq postChatReq)
    {
        // 요청받은 객체 분리
        List<userId> userIdList =postChatReq.getUser_userId();

        // (DB) chat table에 저장

        // 1. max 값으로 id 선정
        Long chatId=chatService.getChatMaxId()+1;

        // 2. chat_num 초기값은 userIdList의 크기


        // (DB) chatuser table에 저장
        chatService.addChatId(chatId);
        chatService.addChatMemberList(chatId, userIdList);
        // 채팅방 객체 생성
        chatService.createRoom(chatId);

        // 삽입한 채팅방 정보 반환
        chatService.getChatData(chatId);
        String result = "채팅방 입장/생성 성공";
        return new BaseResponse<>(result);
    }


    // 디엠 채팅방 목록 확인
    @ResponseBody
    @GetMapping("/room/{userId}")
    public BaseResponse<List<GetChatIdRes>> searchMyChatRoomList(@PathVariable("userId") Long userId){
        List<GetChatIdRes> getChatRoomRes=chatService.getMyChatRoomList(userId);
        return new BaseResponse<>(getChatRoomRes);
    }




    //디엠 메세지 불러오기
    @ResponseBody
    @GetMapping("/chatMessage/{userId}/{chatId}")
    public BaseResponse<List<GetChatRoomInfo>> getChatMessageRes(@PathVariable("userId") Long userId,@PathVariable("chatId") Long chatId) {
        List<GetChatRoomInfo> getChatMessageRes = chatService.getAllChatMessage(userId,chatId);
        return new BaseResponse<>(getChatMessageRes);
    }
}

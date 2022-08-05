package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

interface ChatServiceIF {
    public List<GetChatRoomRes> getAllDataList() throws BaseException;
    public List<GetChatRoomRes> getMyChatRoomList(String userId);
    public Long getChatMaxId();
    public void setChatData(Long chatId);
    public GetChatRoomRes getChatData(Long chatId);
    public void addChatMemberList(Long chatId, List <userId> userIdList);
    public int checkUserIn(Long chatId, Long userId) throws BaseException;
    public void addChatMember(Long chatId, String userId);

    // 채팅방에 유저 추가
    void addChatMember(Long chatId, Long userId);

    public boolean delChatMember(Long chatId, Long userId);
    public void addChatMessage(Long chatId, Long userId,String message);
    public List<GetChatRoomInfo> getAllChatMessage(Long userId, Long chatId);

    // 채팅방 멤버 정보 저장 


}

@Slf4j
@RequiredArgsConstructor
@Service
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChatService implements ChatServiceIF{

    // * Field << 서버 필드 >>
    // ==================================================================
    private final ObjectMapper objectMapper;
    private Map<Long, ChatRoom> ChatRooms;

    private final ChatDao chatDao;
    // ==================================================================
    // * Method << 채팅방 생성 >>
    // ==================================================================
    
    // 채팅방 ID 중 MAX값 찾기
    @Override
    public Long getChatMaxId(){
        // 채팅방 목록이 존재하는 경우
        try{
            return chatDao.getChatMaxId();
        }
        // 채팅방 목록이 존재하지 않는 경우
        catch(Exception e){
            return Long.valueOf(0);
        }
    }

    // 채팅방


    // 채팅방 정보 저장
    public void setChatData(Long chatId){
        chatDao.setChatData(chatId);
    }

    @Override
    public GetChatRoomRes getChatData(Long chatId) {
        return chatDao.getChatData(chatId);
    }


    // 채팅방 멤버 정보 저장 
    @Override
    public void addChatMemberList(Long chatId, List<userId> userIdList) {
        for(int i = 0; i< userIdList.size(); i++){
            Long userId= userIdList.get(i).getUserId();
            chatDao.addChatMember(chatId, userId);
        }
    }

    // 채팅방에 멤버가 입장했다는 메시지 저장

    // 채팅방 객체 생성
    public ChatRoom createRoom(Long chatId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(chatId)
                .build();
        ChatRooms.put(chatId,chatRoom);
        return chatRoom;
    }

    // ==================================================================
    // * Method << 채팅방 목록 확인 >>
    // ==================================================================
    @Override
    public List<GetChatRoomRes> getAllDataList() throws BaseException {
        try {
            return chatDao.getAllDataList();
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Override
    public List<GetChatRoomRes> getMyChatRoomList(String userId) {
        return null;
    }

    // ==================================================================
    // * Method << 참여중인 채팅방 목록 확인 >>
    // ==================================================================
    public List<GetChatIdRes> getMyChatRoomList(Long userId) {
        List<GetChatIdRes> getChatRoomRes = null;
        getChatRoomRes = chatDao.getMyChatRoomList(userId);
        return getChatRoomRes;

    }

    // ==================================================================
    // * Method << 특정 채팅방 정보 확인 >>
    // ==================================================================



    // ==================================================================
    // * Method << 채팅방 입장, 대화, 퇴장 >>
    // ==================================================================

    // 채팅방에 해당 유저가 있는지 확인
    @Override
    public int checkUserIn(Long chatId, Long userId) throws BaseException {
        try{
            int check = chatDao.checkUserIn(chatId,userId);
            return check;
        }
        catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Override
    public void addChatMember(Long chatId, String userId) {

    }

    // 채팅방에 유저 추가
    @Override
    public void addChatMember(Long chatId, Long userId){
        chatDao.addChatMember(chatId, userId); // 유저 추가
        chatDao.PlusChatNum(chatId); // 인원 수 추가
    }

    // 채팅방에 유저 삭제
    @Override
    public boolean delChatMember(Long chatId, Long userId){
        chatDao.delChatMember(chatId, userId);
        chatDao.MinusChatNum(chatId);
        // 채팅방 인원 수가 0이 되서 채팅방 삭제
        if(chatDao.checkChatNum(chatId)==0){
            chatDao.delChatRoom(chatId); // 채팅방 레코드 삭제
            ChatRooms.remove(chatId); // 채팅방 객체 삭제
            return true;
        }
        return false;
    }

    public void addChatMessage(Long chatId, Long userId, String message) {
        chatDao.addChatMessage(chatId, userId, message);
    }


    // 모든 채팅방 메시지 반환
    @Override
    public List<GetChatRoomInfo> getAllChatMessage(Long userId, Long chatId){
        return chatDao.getAllChatMessage(userId,chatId);
    }

    // 채팅방에 메시지 추가
    public void addChatMessage(String type, Long chatId, Long userId, String user_name, String message){
        chatDao.addChatMessage(chatId, userId, message);

    }

    // ==================================================================
    // * Method << 기타 메소드 >>
    // ==================================================================

    // Map 자료구조 초기화
    @PostConstruct
    private void init() {
        ChatRooms = new LinkedHashMap<Long, ChatRoom>();
    }

    // 특정 채팅방 객체 반환
    public ChatRoom findRoomById(Long chatId){
        ChatRoom room = ChatRooms.get(chatId);;

        // 채팅방 객체가 존재하는 경우
        if(room != null)
            return room;
        // 채팅방 객체가 존재하지 않는 경우
        else{
            // chat 테이블에 chatId에 해당하는 채팅방이 있는 경우
            try{
                Long chat_num=chatDao.checkChatNum(chatId);
                return createRoom(chatId); // 채팅방 객체 생성 후 반환
            }
            // chat 테이블에 chatId에 해당하는 채팅방이 없는 경우
            catch(Exception e){
                return null;
            }
        }
    }

    // 메시지 전송
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void addChatId(Long chatId) {
        chatDao.addChatId(chatId);
    }

    public void postMessageLike(PostMessageLikeReq postMessageLikeReq) {
        chatDao.postMessageLike(postMessageLikeReq);
    }
}
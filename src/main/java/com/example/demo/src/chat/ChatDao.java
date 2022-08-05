package com.example.demo.src.chat;

import com.example.demo.src.chat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    private List<GetChatRoomRes> getChatRoomList;
    private List<GetChatMessageRes> messageList;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long getChatMaxId() {
        String getChatMaxIdQuery="select MAX(id) from ChatRoom";
        return this.jdbcTemplate.queryForObject(getChatMaxIdQuery,Long.class);
    }

    public void setChatData(Long chatId) {
        String setChatDataQuery="insert into ChatRoom(id) values(?)";
        this.jdbcTemplate.update(setChatDataQuery,chatId);
    }

    public void addChatMember(Long chatId, Long userId) {
        String addChatMemberQuery="insert into ChatRoomJoin(userId,dmRoomId) values(?,?)";
        Object[] addChatMemberParams=new Object[]{userId,chatId};

        this.jdbcTemplate.update(addChatMemberQuery,addChatMemberParams);
    }

    public List<GetChatRoomRes> getAllDataList() {
        return null;
    }

    public List<GetChatIdRes> getMyChatRoomList(Long userId) {
        String getChatIdQuery="select dmRoomId'chatId' from ChatRoomJoin where userId=?";
        String getMyChatRoomList="select" +
                "       profileImg as 'profileImgUrl',\n" +
                "       name       as 'userName',\n" +
                "       (select dmMessage\n" +
                "        from Message DM\n" +
                "        where DM.dmRoomId = DMR.dmRoomId\n" +
                "        order by DM.createdDate desc\n" +
                "        limit 1)     'lastMessage',\n" +
                "       (select case\n" +
                "                   when TIMESTAMPDIFF(WEEK, DM.createdDate, now()) > 1\n" +
                "                       then concat(TIMESTAMPDIFF(WEEK, DM.createdDate, now()), '주 전')\n" +
                "                   when TIMESTAMPDIFF(hour, DM.createdDate, now()) <= 24\n" +
                "                       then '1일전'\n" +
                "                   when TIMESTAMPDIFF(hour, DM.createdDate, now()) > 48\n" +
                "                       then if(TIMESTAMPDIFF(DAY, DM.createdDate, now()) > 7, date_format(DM.createdDate, '%y-%m-%d'),\n" +
                "                               concat(TIMESTAMPDIFF(DAY, DM.createdDate, now()), '일 전'))\n" +
                "                   when TIMESTAMPDIFF(hour, DM.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, DM.createdDate, now()), '분 전')\n" +
                "                   else concat(TIMESTAMPDIFF(hour, DM.createdDate, now()), '시간 전')\n" +
                "                   end as '게시시간'\n" +
                "        from Message DM\n" +
                "        where DM.dmRoomId = DMR.dmRoomId\n" +
                "        order by DM.createdDate desc\n" +
                "        limit 1)     'lastMessageTime'\n" +
                "\n" +
                "from User\n" +
                "         join ChatRoomJoin DMR on DMR.userId = User.id\n" +
                "where dmRoomId=? and User.id!=?;";

        return this.jdbcTemplate.query(getChatIdQuery,
                (rs,rowNum)->new GetChatIdRes(
                    rs.getLong("chatId")
                , getChatRoomList = this.jdbcTemplate.query(
                        getMyChatRoomList,
                        (rk,rownum)->new GetChatRoomRes(
                        rk.getString("profileImgUrl"),
                        rk.getString("userName"),
                        rk.getString("lastMessage"),
                        rk.getString("lastMessageTime")
        ),rs.getLong("chatId"),userId)),userId);

    }

    public int checkUserIn(Long chatId, Long userId) {
        String checkUserInQuery="select exists (select id from ChatRoomJoin where dmRoomId=? and userId=?)";
        Object[] checkUserIn=new Object[]{chatId,userId};
        return this.jdbcTemplate.queryForObject(checkUserInQuery,
                int.class,
                checkUserIn);
    }

    public void PlusChatNum(Long chatId) {

    }

    public void delChatMember(Long chatId, Long userId) {
    }

    public void MinusChatNum(Long chatId) {
    }

    public Long checkChatNum(Long chatId) {
        return chatId;
    }

    public void delChatRoom(Long chatId) {
    }

    public List<GetChatRoomInfo> getAllChatMessage(Long userId, Long chatId) {
        String getChatMessageQuery="select U.id                       'userId',\n" +
                "       U.profileImg               'profileImgUrl',\n" +
                "       M.id                       'messageId',\n" +
                "       dmMessage'message',\n" +
                "       M.createdDate'messageTime',\n" +
                "       (select exists (select id from MessageLike where messageId = M.id))'likeCheck'\n" +
                "from User U\n" +
                "         join Message M on M.userId = U.id\n" +
                "where M.dmRoomId = ? \n" +
                "order by M.createdDate desc;";
        String getChatInfoQuery ="select dmRoomId 'chatId', U.id 'userId', U.profileImg 'profileImgUrl', U.name'userName', U.userId 'userLoginId'\n" +
                "from ChatRoomJoin CRJ\n" +
                "         join User U on CRJ.userId = U.id\n" +
                "where CRJ.userId != ?\n" +
                "  and dmRoomId = ?";
        Object[] getChatInfoParams = new Object[]{
                userId,chatId
        };
        return this.jdbcTemplate.query(getChatInfoQuery,
                (rs,rowNum)->new GetChatRoomInfo(
                        rs.getLong("chatId"),
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userName"),
                        rs.getString("userLoginId"),
                        messageList = this.jdbcTemplate.query(
                                getChatMessageQuery,
                                (rk,rownum)->new GetChatMessageRes(
                                        rk.getLong("userId"),
                                        rk.getString("profileImgUrl"),
                                        rk.getLong("messageId"),
                                        rk.getString("message"),
                                        rk.getString("messageTime"),
                                        rk.getInt("likeCheck")
                                ),chatId)),getChatInfoParams);

    }

    public void addChatMessage(Long chatId, Long userId, String message) {
        String addChatMessagerQuery="insert into Message(dmRoomId,userId,dmMessage) values(?,?,?)";
        Object[] addChatMessageParams=new Object[]{chatId,userId,message};
        this.jdbcTemplate.update(addChatMessagerQuery,addChatMessageParams);
    }

    public GetChatRoomRes getChatData(Long chatId) {
        return null;
    }

    public String getUserName(Long userId) {
        return "";
    }

    public void addChatId(Long chatId) {
        String addChatIdQuery="insert into ChatRoom(id) values(?)";
        this.jdbcTemplate.update(addChatIdQuery,chatId);
    }

    public void postMessageLike(PostMessageLikeReq postMessageLikeReq) {
        String postMessageLikeQuery="insert into MessageLike(dmMessageId,userId) values(?,?)";
        Object[] params = new Object[]{
                postMessageLikeReq.getMessageId(),postMessageLikeReq.getUserId()
        };
        this.jdbcTemplate.update(postMessageLikeQuery,params);
    }
}

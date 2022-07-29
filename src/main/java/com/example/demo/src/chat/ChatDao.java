package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChatIdRes;
import com.example.demo.src.chat.model.GetChatMessageRes;
import com.example.demo.src.chat.model.GetChatRoomRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    private List<GetChatRoomRes> getChatRoomList;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long getChatMaxId() {
        String getChatMaxIdQuery="select MAX(id) from DMRoom";
        return this.jdbcTemplate.queryForObject(getChatMaxIdQuery,Long.class);
    }

    public void setChatData(Long chatId) {
        String setChatDataQuery="insert into DMRoom(id) values(?)";
        this.jdbcTemplate.update(setChatDataQuery,chatId);
    }

    public void addChatMember(Long chatId, Long userId) {
        String addChatMemberQuery="insert into DMRoomJoin(userId,dmRoomId) values(?,?)";
        Object[] addChatMemberParams=new Object[]{userId,chatId};

        this.jdbcTemplate.update(addChatMemberQuery,addChatMemberParams);
    }

    public List<GetChatRoomRes> getAllDataList() {
        return null;
    }

    public List<GetChatIdRes> getMyChatRoomList(Long userId) {
        String getChatIdQuery="select dmRoomId'chatId' from DMRoomJoin where userId=?";
        String getMyChatRoomList="select" +
                "       profileImg as 'profileImgUrl',\n" +
                "       name       as 'userName',\n" +
                "       (select dmMessage\n" +
                "        from DMMessage DM\n" +
                "        where DM.dmRoomId = DMR.dmRoomId\n" +
                "        order by DM.createdDate desc\n" +
                "        limit 1)     'lastMessage',\n" +
                "       (select case\n" +
                "                   when TIMESTAMPDIFF(WEEK, DM.createdDate, now()) > 1\n" +
                "                       then concat(TIMESTAMPDIFF(WEEK, DM.createdDate, now()), '주 전')\n" +
                "                   when TIMESTAMPDIFF(hour, DM.createdDate, now()) >= 24\n" +
                "                       then '1일전'\n" +
                "                   when TIMESTAMPDIFF(hour, DM.createdDate, now()) > 48\n" +
                "                       then if(TIMESTAMPDIFF(DAY, DM.createdDate, now()) > 7, date_format(DM.createdDate, '%y-%m-%d'),\n" +
                "                               concat(TIMESTAMPDIFF(DAY, DM.createdDate, now()), '일 전'))\n" +
                "                   when TIMESTAMPDIFF(hour, DM.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, DM.createdDate, now()), '분 전')\n" +
                "                   else concat(TIMESTAMPDIFF(hour, DM.createdDate, now()), '시간 전')\n" +
                "                   end as '게시시간'\n" +
                "        from DMMessage DM\n" +
                "        where DM.dmRoomId = DMR.dmRoomId\n" +
                "        order by DM.createdDate desc\n" +
                "        limit 1)     'lastMessageTime'\n" +
                "\n" +
                "from User\n" +
                "         join DMRoomJoin DMR on DMR.userId = User.id\n" +
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
        String checkUserInQuery="select exists (select id from DMRoomJoin where dmRoomId=? and userId=?)";
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

    public List<GetChatMessageRes> getAllChatMessage(Long chatId) {
        return null;
    }

    public void addChatMessage(Long chatId, Long userId, String message) {
        String addChatMessagerQuery="insert into DMMessage(dmRoomId,userId,dmMessage) values(?,?,?)";
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
        String addChatIdQuery="insert into DMRoom(id) values(?)";
        this.jdbcTemplate.update(addChatIdQuery,chatId);
    }
}

package com.example.demo.src.admin;

import com.example.demo.src.admin.model.*;
import com.example.demo.src.board.model.GetBoardImgRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AdminDao {
    private JdbcTemplate jdbcTemplate;
    private List<BoardImg> boardImgList;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(GetUserReq getUserReq) {
        String getUsersQuery=String.format("select id'userId',userId'userLoginId',phoneNumber,concat(YEAR(logInDate),'.',MONTH(logInDate),'.',DAY(logInDate))'logInDate' from User " +
                "where id>0 %s %s %s %s order by logInDate desc limit ?,?",getUserReq.getUserIdQuery(),getUserReq.getNameQuery(),getUserReq.getStatusQuery(),getUserReq.getDateQuery());
        Object[] paging=new Object[]{
                (getUserReq.getPaging()-1)*10,(getUserReq.getPaging())*10
        };
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum)->new GetUserRes(
                        rs.getLong("userId"),
                        rs.getString("userLoginId"),
                        rs.getString("phoneNumber"),
                        rs.getString("logInDate")
                ),paging);

    }

    public List<GetUserInfoRes> getUserInfo(Long userId) {
        String getUserInfoQuery="select * from User where id=?";
        return this.jdbcTemplate.query(getUserInfoQuery,
                (rs,rowNum)->new GetUserInfoRes(
                        rs.getLong("id"),
                        rs.getString( "userId"),
                        rs.getString( "profileImg"),
                        rs.getDate("birth"),
                        rs.getString( "password"),
                        rs.getString( "name"),
                        rs.getString( "webSite"),
                        rs.getString( "introduce"),
                        rs.getString( "phoneNumber"),
                        rs.getString( "userReact"),
                        rs.getString("userStatus"),
                        rs.getDate("createdDate"),
                        rs.getDate("updatedDate"),
                        rs.getDate("logInDate"),
                        rs.getString( "userPublic"),
                        rs.getString( "agreeInfo"),
                        rs.getString( "dropStatus"),
                        rs.getString( "suspensionStatus")
                ),userId);
    }


    public void userSuspension(Long userId) {
        String userSuspensionQuery ="update User set suspensionStatus='TRUE' where id=?";
        this.jdbcTemplate.update(userSuspensionQuery,userId);
    }
    public List<GetBoardRes> getBoards(GetBoardReq getBoardReq){
        String getBoardQuery=String.format("select B.id                                                                            'boardId',\n" +
                "       U.userId                                                                        'userLoginId',\n" +
                "       description,\n" +
                "       concat(YEAR(B.createdDate), '.', MONTH(B.createdDate), '.', DAY(B.createdDate)) 'boardDate'\n" +
                "from Board B\n" +
                "         join User U on B.userId = U.id\n" +
                "where B.id>0 %s %s %s order by B.createdDate desc limit ?,?;",getBoardReq.getUserIdQuery(),getBoardReq.getDateQuery(),getBoardReq.getStatusQuery());
        Object[] paging=new Object[]{
                (getBoardReq.getPaging()-1)*10,(getBoardReq.getPaging())*10
        };

        return this.jdbcTemplate.query(getBoardQuery,
                (rs,rowNum)->new GetBoardRes(
                        rs.getLong("boardId"),
                        rs.getString("userLoginId"),
                        rs.getString("description"),
                        rs.getString("boardDate")
                ),paging);
    }

    public List<GetBoardInfoRes> getBoardInfo(Long boardId) {
        String getBoardInfoQuery="select id'boardId',userId,description,createdDate,updatedDate,suspensionStatus,status,dropStatus from Board where id=?";
        String getBoardImgQuery="select BI.id 'imgId', BI.boardImgurl 'imgUrl'\n" +
                "from BoardImg BI\n" +
                "         join Board B on B.id = BI.boardId\n" +
                "where BI.boardId = ?";
        return this.jdbcTemplate.query(getBoardInfoQuery,
                ((rs, rowNum) -> new GetBoardInfoRes(
                        rs.getLong("boardId"),
                        rs.getLong("userId"),
                        rs.getString("description"),
                        rs.getDate("createdDate"),
                        rs.getDate("updatedDate"),
                        rs.getString("suspensionStatus"),
                        rs.getString("status"),
                        rs.getString("dropStatus"),
                        boardImgList=this.jdbcTemplate.query(getBoardImgQuery,
                                (rk,rownum)->new BoardImg(
                                        rk.getLong("imgId"),
                                        rk.getString("imgUrl")
                                ),boardId)
                )),boardId);
    }
}

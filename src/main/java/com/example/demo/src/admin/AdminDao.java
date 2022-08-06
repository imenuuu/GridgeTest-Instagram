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
                        rs.getTimestamp("createdDate"),
                        rs.getTimestamp("updatedDate"),
                        rs.getTimestamp("logInDate"),
                        rs.getString( "userPublic"),
                        rs.getString( "agreeInfo"),
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
        String getBoardInfoQuery="select id'boardId',userId,description,createdDate,updatedDate,suspensionStatus,status from Board where id=?";
        String getBoardImgQuery="select BI.id 'imgId', BI.boardImgurl 'imgUrl'\n" +
                "from BoardImg BI\n" +
                "         join Board B on B.id = BI.boardId\n" +
                "where BI.boardId = ?";
        return this.jdbcTemplate.query(getBoardInfoQuery,
                ((rs, rowNum) -> new GetBoardInfoRes(
                        rs.getLong("boardId"),
                        rs.getLong("userId"),
                        rs.getString("description"),
                        rs.getTimestamp("createdDate"),
                        rs.getTimestamp("updatedDate"),
                        rs.getString("suspensionStatus"),
                        rs.getString("status"),
                        boardImgList=this.jdbcTemplate.query(getBoardImgQuery,
                                (rk,rownum)->new BoardImg(
                                        rk.getLong("imgId"),
                                        rk.getString("imgUrl")
                                ),boardId)
                )),boardId);
    }

    public List<GetBoardReportRes> getBoardReport(int paging) {
        String getBoardReportQuery="select BR.id'reportId' ,U.name'userName',B.id'boardId',B.description,RL.descrption'cause',BR.createdDate from BoardReport BR\n" +
                "join Board B on B.id=BR.boardId\n" +
                "join User U on U.id=B.userId\n" +
                "join ReportList RL on RL.id=BR.reportId limit ?,?";
        Object[] pagingParams=new Object[]{
                (paging-1)*10,(paging)*10
        };
        return this.jdbcTemplate.query(getBoardReportQuery,
                (rs,rowNum)-> new GetBoardReportRes(
                        rs.getLong("reportId"),
                        rs.getString("userName"),
                        rs.getLong("boardId"),
                        rs.getString("description"),
                        rs.getString("cause"),
                        rs.getTimestamp("createdDate")
                ),pagingParams);
    }

    public List<GetReCommentReportRes> getReCommentReport(int paging) {
        String getBoardReportQuery="select CR.id'reportId' ,U.name'userName',C.id'reCommentId',C.reComment,RL.descrption'cause',CR.createdDate from ReCommentReport CR\n" +
                "join ReComment C on C.id=CR.reCommentId\n" +
                "join User U on U.id=C.userId\n" +
                "join ReportList RL on RL.id=CR.reportId limit ?,?";
        Object[] pagingParams=new Object[]{
                (paging-1)*10,(paging)*10
        };
        return this.jdbcTemplate.query(getBoardReportQuery,
                (rs,rowNum)-> new GetReCommentReportRes(
                        rs.getLong("reportId"),
                        rs.getString("userName"),
                        rs.getLong("reCommentId"),
                        rs.getString("reComment"),
                        rs.getString("cause"),
                        rs.getTimestamp("createdDate")
                ),pagingParams);
    }

    public List<GetCommentReportRes> getCommentReport(int paging) {
        String getBoardReportQuery="select CR.id'reportId' ,U.name'userName',C.id'commentId',C.comment,RL.descrption'cause',CR.createdDate from CommentReport CR\n" +
                "join Comment C on C.id=CR.commentId\n" +
                "join User U on U.id=C.userId\n" +
                "join ReportList RL on RL.id=CR.reportId limit ?,?";
        Object[] pagingParams=new Object[]{
                (paging-1)*10,(paging)*10
        };
        return this.jdbcTemplate.query(getBoardReportQuery,
                (rs,rowNum)-> new GetCommentReportRes(
                        rs.getLong("reportId"),
                        rs.getString("userName"),
                        rs.getLong("commentId"),
                        rs.getString("comment"),
                        rs.getString("cause"),
                        rs.getTimestamp("createdDate")
                ),pagingParams);
    }

    public void deleteCommentReport(Long reportId) {
        String deleteQuery = "delete from CommentReport where id=?";
        this.jdbcTemplate.update(deleteQuery,reportId);
    }

    public void deleteBoardReport(Long reportId) {
        String deleteQuery = "delete from BoardReport where id=?";
        this.jdbcTemplate.update(deleteQuery,reportId);

    }

    public void deleteReCommentReport(Long reportId) {
        String deleteQuery = "delete from ReCommentReport where id=?";
        this.jdbcTemplate.update(deleteQuery,reportId);
    }

    public List<GetBoardReportInfoRes> getBoardReportInfo(Long boardId) {
        String getBoardInfoQuery="select Board.id'boardId',Board.userId,description,Board.createdDate,updatedDate,suspensionStatus,status,RL.descrption'cause' from Board " +
                "join BoardReport BR on BR.boardId=Board.id " +
                "join ReportList RL on BR.reportId = RL.id " +
                "where Board.id=?";
        String getBoardImgQuery="select BI.id 'imgId', BI.boardImgurl 'imgUrl'\n" +
                "from BoardImg BI\n" +
                "         join Board B on B.id = BI.boardId\n" +
                "where BI.boardId = ?";
        return this.jdbcTemplate.query(getBoardInfoQuery,
                ((rs, rowNum) -> new GetBoardReportInfoRes(
                        rs.getLong("boardId"),
                        rs.getLong("userId"),
                        rs.getString("description"),
                        rs.getTimestamp("createdDate"),
                        rs.getTimestamp("updatedDate"),
                        rs.getString("suspensionStatus"),
                        rs.getString("status"),
                        rs.getString("cause"),
                        boardImgList=this.jdbcTemplate.query(getBoardImgQuery,
                                (rk,rownum)->new BoardImg(
                                        rk.getLong("imgId"),
                                        rk.getString("imgUrl")
                                ),boardId)
                )),boardId);
    }

    public void deleteBoard(Long boardId) {
        String deleteQuery = "update User set suspensionStatus='TRUE' where id=?";
        this.jdbcTemplate.update(deleteQuery,boardId);
    }

    public void deleteComment(Long commentId) {
        String deleteQuery = "update Comment set suspensionStatus='TRUE' where id=?";
        this.jdbcTemplate.update(deleteQuery,commentId);
    }

    public void deleteReComment(Long reCommentId) {
        String deleteQuery = "update ReComment set suspensionStatus='TRUE' where id=?";
        this.jdbcTemplate.update(deleteQuery,reCommentId);
    }

    public List<GetLogRes> getBoardLog(GetLogQueryReq getLogQueryReq) {
        String getBoardLogQuery = String.format("select BL.id'logId',U.userId'userLoginId',type,BL.createdDate'logCreated' " +
                "from BoardLog BL join User U on U.id=BL.userId where BL.id>0 %s %s order by BL.createdDate desc limit ?,?",getLogQueryReq.getTypeQuery(),getLogQueryReq.getDateQuery());
        Object[] pagingParams=new Object[]{
                (getLogQueryReq.getPaging()-1)*10,getLogQueryReq.getPaging()*10
        };
        return this.jdbcTemplate.query(getBoardLogQuery,
                (rs, rowNum) -> new GetLogRes(
                        rs.getLong("logId"),
                        rs.getString("userLoginId"),
                        rs.getString("type"),
                        rs.getTimestamp("logCreated")
                ),pagingParams);

    }

    public List<GetLogRes> getCommentLog(GetLogQueryReq getLogQueryReq) {
        String getCommentLogQuery = String.format("select CL.id'logId',U.userId'userLoginId',type,CL.createdDate'logCreated' " +
                "from CommentLog CL join User U on U.id=CL.userId where CL.id>0 %s %s order by CL.createdDate desc limit ?,?",getLogQueryReq.getTypeQuery(),getLogQueryReq.getDateQuery());
        Object[] pagingParams=new Object[]{
                (getLogQueryReq.getPaging()-1)*10,getLogQueryReq.getPaging()*10
        };
        return this.jdbcTemplate.query(getCommentLogQuery,
                (rs, rowNum) -> new GetLogRes(
                        rs.getLong("logId"),
                        rs.getString("userLoginId"),
                        rs.getString("type"),
                        rs.getTimestamp("logCreated")
                ),pagingParams);
    }

    public List<GetLogRes> getReCommentLog(GetLogQueryReq getLogQueryReq) {
        String getCommentLogQuery = String.format("select CL.id'logId',U.userId'userLoginId',type,CL.createdDate'logCreated' " +
                "from ReCommentLog CL join User U on U.id=CL.userId where CL.id>0 %s %s order by CL.createdDate desc limit ?,?",getLogQueryReq.getTypeQuery(),getLogQueryReq.getDateQuery());
        Object[] pagingParams=new Object[]{
                (getLogQueryReq.getPaging()-1)*10,getLogQueryReq.getPaging()*10
        };
        return this.jdbcTemplate.query(getCommentLogQuery,
                (rs, rowNum) -> new GetLogRes(
                        rs.getLong("logId"),
                        rs.getString("userLoginId"),
                        rs.getString("type"),
                        rs.getTimestamp("logCreated")
                ),pagingParams);
    }

    public List<GetLogRes> getReportLog(GetLogQueryReq getLogQueryReq) {
        String getReportLogQuery = String.format("select RL.id'logId',U.userId'userLoginId',type,RL.createdDate'logCreated' " +
                "from ReportLog RL join User U on U.id=RL.userId where RL.id>0 %s %s order by RL.createdDate desc limit ?,?",getLogQueryReq.getTypeQuery(),getLogQueryReq.getDateQuery());
        Object[] pagingParams=new Object[]{
                (getLogQueryReq.getPaging()-1)*10,getLogQueryReq.getPaging()*10
        };
        return this.jdbcTemplate.query(getReportLogQuery,
                (rs, rowNum) -> new GetLogRes(
                        rs.getLong("logId"),
                        rs.getString("userLoginId"),
                        rs.getString("type"),
                        rs.getTimestamp("logCreated")
                ),pagingParams);

    }

    public List<GetLogRes> getUserLog(GetLogQueryReq getLogQueryReq) {

        String getUserLogQuery = String.format("select UL.id'logId',U.userId'userLoginId',type,UL.createdDate'logCreated' " +
                "from UserLog UL join User U on U.id=UL.userId where UL.id>0 %s %s order by UL.createdDate desc limit ?,?",getLogQueryReq.getTypeQuery(),getLogQueryReq.getDateQuery());
        Object[] pagingParams=new Object[]{
                (getLogQueryReq.getPaging()-1)*10,getLogQueryReq.getPaging()*10
        };
        return this.jdbcTemplate.query(getUserLogQuery,
                (rs, rowNum) -> new GetLogRes(
                        rs.getLong("logId"),
                        rs.getString("userLoginId"),
                        rs.getString("type"),
                        rs.getTimestamp("logCreated")
                ),pagingParams);

    }

    public int checkBoardReport(Long reportId) {
        String checkQuery = "select exists(select id from BoardReport where id=? )";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,reportId);
    }

    public int checkCommentReport(Long reportId) {
        String checkQuery = "select exists(select id from CommentReport where id=? )";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,reportId);
    }

    public int checkReCommentReport(Long reportId) {
        String checkQuery = "select exists(select id from ReCommentReport where id=? )";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,reportId);
    }
}

package com.example.demo.src.board;

import com.example.demo.src.board.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BoardDao {
    private JdbcTemplate jdbcTemplate;

    List<GetBoardImgRes> boardImgList;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long createBoard(PostBoardReq postBoardReq) {
        String createBoardQuery="insert into Board(userId,description) values(?,?) ";
        Object[] createBoardParams = new Object[]{
                postBoardReq.getUserId(),postBoardReq.getDescription()}
                ;

        this.jdbcTemplate.update(createBoardQuery,createBoardParams);

        String lastInsertIdQuery="select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);

    }

    public void createBoardImg(Long userId, Long lastInsertId, String boardImgUrl) {
        String createBoardImgQuery="insert into BoardImg(userId,boardId,boardImgUrl) values(?,?,?)";
        Object[] createBoardImgParams = new Object[]{
                userId,lastInsertId,boardImgUrl
        };

        this.jdbcTemplate.update(createBoardImgQuery,createBoardImgParams);
    }

    public List<GetBoardRes> getMainBoard(Long userId,int paging) {

        String getMainboardQuery="select U.id as'userId',U.profileImg 'profileImgUrl',\n" +
                "       \n" +
                "       U.userid as 'userLoginId',\n" +
                "       B.id                                                            'boardId',\n" +
                "       B.description,\n" +
                "       (select exists(select BL.id from BoardLike BL where BL.boardId=B.id and BL.userId=F.userId))'likeCheck',\n" +
                "       (select count(BL.id) from BoardLike BL where BL.boardId = B.id and BL.status='TRUE') 'likeCnt',\n" +
                "       ((select count(C.id) from Comment C where C.boardId=B.id and C.status='TRUE')+\n" +
                "       (select count(RC.id) from ReComment RC join Comment C on C.id=RC.id where C.boardId=B.id and RC.status='TRUE'))'commentCnt',\n" +
                "\n" +
                "       case\n" +
                "           when YEAR(B.createdDate) < YEAR(now())\n" +
                "               then concat(YEAR(B.createdDate), '년 ', MONTH(B.createdDate), '월 ', DAY(B.createdDate), '일')\n" +
                "           when YEAR(B.createdDate) = YEAR(now()) then\n" +
                "               case\n" +
                "                   when (TIMESTAMPDIFF(DAY, B.createdDate, now())) > 7\n" +
                "                       then concat(month(B.createdDate), '월 ', DAY(B.createdDate), '일')\n" +
                "                   when TIMESTAMPDIFF(minute, B.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(second, B.createdDate, now()),'초 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) > 24\n" +
                "                       then concat(TIMESTAMPDIFF(DAY, B.createdDate, now()), '일 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, B.createdDate, now()), '분 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) < 24\n" +
                "                       then concat(TIMESTAMPDIFF(hour, B.createdDate, now()), '시간 전')\n" +
                "                   end end as                                          boardTime\n" +
                "from User U\n" +
                "         join Board B on B.userId = U.id\n" +
                "         join Following F on F.followUserId = U.id\n" +
                "where F.userId = ? and B.status='TRUE' and B.suspensionStatus='FALSE' \n" +
                "order by B.createdDate desc limit ?,?;";
        String getBoardImgQuery="select BI.id 'imgId', BI.boardImgurl 'imgUrl'\n" +
                "from BoardImg BI\n" +
                "         join Board B on B.id = BI.boardId\n" +
                "where BI.boardId = ?";
        Object[] getBoardListParams = new Object[]{
                userId,(paging-1)*10,paging*10
        };
        return this.jdbcTemplate.query(getMainboardQuery,
                (rs,rowNum) ->new GetBoardRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userLoginId"),
                        rs.getLong("boardId"),
                        rs.getString("description"),
                        rs.getInt("likeCheck"),
                        rs.getInt("likeCnt"),
                        rs.getInt("commentCnt"),
                        rs.getString("boardTime"),
                        boardImgList=this.jdbcTemplate.query(getBoardImgQuery,
                                (rk,rownum)->new GetBoardImgRes(
                                        rk.getLong("imgId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("boardId"))
                ),getBoardListParams);
    }

    public void patchBoard(PatchBoardReq patchBoardReq) {
        String patchBoardQuery="update Board set description=? where id=?";
        Object[] patchBoardParams = new Object[]{
                patchBoardReq.getDescription(),patchBoardReq.getBoardId()
        };
        this.jdbcTemplate.update(patchBoardQuery,patchBoardParams);
    }

    public void postBoardReport(PostBoardReportReq postBoardReportReq) {
        String postBoardReport="insert into BoardReport(userId,boardId,reportId) values(?,?,?)";
        Object[] postBoardReportParams = new Object[]{postBoardReportReq.getUserId(),postBoardReportReq.getBoardId(),postBoardReportReq.getReportId()};
        this.jdbcTemplate.update(postBoardReport,postBoardReportParams);
    }

    public Long checkBoardUserId(Long boardId) {
        String getUserIdQuery="select userId from Board where id=?";
        return this.jdbcTemplate.queryForObject(getUserIdQuery,Long.class,boardId);
    }

    public List<GetBoardRes> getProfileBoard(Long userId, Long profileUserId, int paging) {

        String getMainboardQuery="select U.profileImg'profileImgUrl',\n" +
                "       U.id as'userId',\n" +
                "       U.userid as 'userLoginId',\n" +
                "       B.id                                                            'boardId',\n" +
                "       B.description,\n" +
                "       (select exists(select BL.id from BoardLike BL where BL.boardId=B.id and BL.userId=?))'likeCheck',\n" +
                "       (select count(BL.id) from BoardLike BL where BL.boardId = B.id and BL.status='TRUE') 'likeCnt',\n" +
                "       ((select count(C.id) from Comment C where C.boardId=B.id and C.status='TRUE')+\n" +
                "       (select count(RC.id) from ReComment RC join Comment C on C.id=RC.id where C.boardId=B.id and RC.status='TRUE'))'commentCnt',\n" +
                "       case\n" +
                "           when YEAR(B.createdDate) < YEAR(now())\n" +
                "               then concat(YEAR(B.createdDate), '년 ', MONTH(B.createdDate), '월 ', DAY(B.createdDate), '일')\n" +
                "           when YEAR(B.createdDate) = YEAR(now()) then\n" +
                "               case\n" +
                "                   when (TIMESTAMPDIFF(DAY, B.createdDate, now())) > 7\n" +
                "                       then concat(month(B.createdDate), '월 ', DAY(B.createdDate), '일')\n" +
                "                   when TIMESTAMPDIFF(minute, B.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(second, B.createdDate, now()),'초 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) > 24\n" +
                "                       then concat(TIMESTAMPDIFF(DAY, B.createdDate, now()), '일 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, B.createdDate, now()), '분 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) < 24\n" +
                "                       then concat(TIMESTAMPDIFF(hour, B.createdDate, now()), '시간 전')\n" +
                "                   end end as                                          boardTime\n" +
                "from User U\n" +
                "         join Board B on B.userId = U.id\n" +
                "where B.userId=? and B.status='TRUE' and B.suspensionStatus='FALSE' \n" +
                "order by B.createdDate desc limit ?,?;";
        String getBoardImgQuery="select BI.id 'imgId', BI.boardImgurl 'imgUrl'\n" +
                "from BoardImg BI\n" +
                "         join Board B on B.id = BI.boardId\n" +
                "where BI.boardId = ?";
        Object[] getBoardListParams = new Object[]{
                userId,profileUserId,(paging-1)*10,paging*10
        };
        return this.jdbcTemplate.query(getMainboardQuery,
                (rs,rowNum) ->new GetBoardRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userLoginId"),
                        rs.getLong("boardId"),
                        rs.getString("description"),
                        rs.getInt("likeCheck"),
                        rs.getInt("likeCnt"),
                        rs.getInt("commentCnt"),
                        rs.getString("boardTime"),
                        boardImgList=this.jdbcTemplate.query(getBoardImgQuery,
                                (rk,rownum)->new GetBoardImgRes(
                                        rk.getLong("imgId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("boardId"))
                ),getBoardListParams);
    }

    public int checkBoardLike(Long userId, Long boardId) {
        String checkQuery = "select exists(select id from BoardLike where userId=? and boardId=?)";
        Object[] checkParams = new Object[]{userId,boardId};
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,checkParams);
    }
    public void postBoardLike(Long userId, Long boardId){
        String postBoardLikeQuery="insert into BoardLike(userId,boardId) values(?,?)";
        Object[] postBoardLikeParams = new Object[]{userId,boardId};
        this.jdbcTemplate.update(postBoardLikeQuery,postBoardLikeParams);
    }
    public void deleteBoardLike(Long userId, Long boardId){
        String deleteBoardLikeQuery="delete from BoardLike where userId=? and boardId=?";
        Object[] deleteBoardLikeParams = new Object[]{userId,boardId};
        this.jdbcTemplate.update(deleteBoardLikeQuery,deleteBoardLikeParams);
    }

    public int checkBoard(Long boardId) {
        String checkQuery = "select exists(select id from Board where id=? and status='TRUE')";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,boardId);
    }

    public void deleteBoard(Long boardId) {
        String deleteQuery = "update Board set status='FALSE' where id=?";
        this.jdbcTemplate.update(deleteQuery,boardId);
    }

    public void createLog(PostLogReq postLogReq) {
        String createLogQuery="insert into BoardLog (type,userId) values(?,?)";
        Object[] createLog = new Object[]{postLogReq.getType(),postLogReq.getUserId()};
        this.jdbcTemplate.update(createLogQuery,createLog);
    }

    public void createReportLog(PostLogReq postLogReq) {
        String createLogQuery="insert into ReportLog (type,userId) values(?,?)";
        Object[] createLog = new Object[]{postLogReq.getType(),postLogReq.getUserId()};
        this.jdbcTemplate.update(createLogQuery,createLog);
    }
}

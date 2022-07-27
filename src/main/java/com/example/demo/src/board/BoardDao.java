package com.example.demo.src.board;

import com.example.demo.src.board.model.GetBoardImgRes;
import com.example.demo.src.board.model.GetBoardRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BoardDao {
    private JdbcTemplate jdbcTemplate;

    List<GetBoardImgRes> getBoardImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long createBoard(Long userId, String description) {
        String createBoardQuery="insert into Board(userId,description) values(?,?) ";
        Object[] createBoardParams = new Object[]{userId,description};

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

        String getMainboardQuery="select U.profileImg 'profileImgUrl',\n" +
                "       U.id as'userId',\n" +
                "       U.userid as 'userName',\n" +
                "       B.id                                                            'boardId',\n" +
                "       B.description,\n" +
                "       (select exists(select BL.id from BoardLike BL where BL.boardId=B.id and BL.userId=F.userId))'likeCheck',\n" +
                "       (select count(BL.id) from BoardLike BL where BL.boardId = B.id) 'likeCnt',\n" +
                "       ((select count(C.id) from Comment C where C.boardId=B.id)+\n" +
                "       (select count(RC.id) from ReComment RC where RC.boardId=B.id))'commentCnt',\n" +
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
                "where F.userId = ?\n" +
                "order by B.createdDate desc limit ?,?;";
        String getBoardImgQuery="select BI.id 'imgId', BI.boardImgurl 'imgUrl'\n" +
                "from BoardImg BI\n" +
                "         join Board B on B.id = BI.boardId\n" +
                "where BI.boardId = ?";
        return this.jdbcTemplate.query(getMainboardQuery,
                (rs,rowNum) ->new GetBoardRes(
                        rs.getString("profileImgUrl"),
                        rs.getLong("userId"),
                        rs.getString("userName"),
                        rs.getLong("boardId"),
                        rs.getString("description"),
                        rs.getInt("likeCheck"),
                        rs.getInt("likeCnt"),
                        rs.getInt("commentCnt"),
                        rs.getString("boardTime"),
                        getBoardImgRes=this.jdbcTemplate.query(getBoardImgQuery,
                                (rk,rownum)->new GetBoardImgRes(
                                        rk.getLong("imgId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("boardId"))
                ),userId);
    }
}
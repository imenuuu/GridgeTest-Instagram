package com.example.demo.src.comment;

import com.example.demo.src.comment.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {
    private JdbcTemplate jdbcTemplate;

    List<GetCommentRes> commentList;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<GetCommentInfo> getComment(Long userId, Long boardId, int paging) {
        String getBoardInfoQuery="\n" +
                "select U.id                                                                'userId',\n" +
                "       U.profileImg                                                        'profileImgUrl',\n" +
                "       U.userId                                                            'userLoginId',\n" +
                "       description,\n" +
                "               case\n" +
                "                   when TIMESTAMPDIFF(WEEK, B.createdDate, now())>1\n" +
                "                   then concat(TIMESTAMPDIFF(WEEK, B.createdDate, now()),'주 전')\n" +
                "                   when TIMESTAMPDIFF(minute, B.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(second, B.createdDate, now()),'초 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) > 24\n" +
                "                       then concat(TIMESTAMPDIFF(DAY, B.createdDate, now()), '일 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, B.createdDate, now()), '분 전')\n" +
                "                   when TIMESTAMPDIFF(hour, B.createdDate, now()) < 24\n" +
                "                       then concat(TIMESTAMPDIFF(hour, B.createdDate, now()), '시간 전')\n" +
                "                   end as 'boardTime'\n" +
                "from User U\n" +
                "join Board B on B.userId = U.id\n" +
                "where B.id=?";
        String getCommentQuery="select U.id                                                                'userId',\n" +
                "       U.profileImg                                                        'profileImgUrl',\n" +
                "       U.userId                                                            'userLoginId',\n" +
                "       C.id'commentId',\n" +
                "       comment,\n" +
                "               case\n" +
                "                   when TIMESTAMPDIFF(WEEK, C.createdDate, now())>1\n" +
                "                   then concat(TIMESTAMPDIFF(WEEK, C.createdDate, now()),'주 전')\n" +
                "                   when TIMESTAMPDIFF(minute, C.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(second, C.createdDate, now()),'초 전')\n" +
                "                   when TIMESTAMPDIFF(hour, C.createdDate, now()) > 24\n" +
                "                       then concat(TIMESTAMPDIFF(DAY, C.createdDate, now()), '일 전')\n" +
                "                   when TIMESTAMPDIFF(hour, C.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, C.createdDate, now()), '분 전')\n" +
                "                   when TIMESTAMPDIFF(hour, C.createdDate, now()) < 24\n" +
                "                       then concat(TIMESTAMPDIFF(hour, C.createdDate, now()), '시간 전')\n" +
                "                   end as 'commentTime',\n" +
                "       (select count(CL.id) from CommentLike CL where CL.commentId = C.id) 'likeCnt',\n" +
                "       (select count(RC.id) from ReComment RC where RC.commentId = C.id) 'reCommentCnt'," +
                "(select exists(select id from CommentLike CL where CL.commentId=C.id and CL.userId=?))'likeCheck'\n" +
                "from User U\n" +
                "         join Comment C on C.userId = U.id\n" +
                "where C.boardId = ? order by C.createdDate desc limit ?,?;";
        Object[] getCommentParams = new Object[]{userId,boardId,(paging-1)*10,paging*10};
        return this.jdbcTemplate.query(getBoardInfoQuery,
                (rs,rowNum)->new GetCommentInfo(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userLoginId"),
                        rs.getString("description"),
                        rs.getString("boardTime"),
                        commentList=this.jdbcTemplate.query(getCommentQuery,
                                (rk,rownum)->new GetCommentRes(
                                        rk.getLong("userId"),
                                        rk.getString("profileImgUrl"),
                                        rk.getString("userLoginId"),
                                        rk.getLong("commentId"),
                                        rk.getString("comment"),
                                        rk.getString("commentTime"),
                                        rk.getInt("likeCnt"),
                                        rk.getInt("reCommentCnt"),
                                        rk.getInt("likeCheck")
                                ),getCommentParams)
                ),boardId);
    }

    public void postComment(PostCommentReq postCommentReq) {
        String postCommentQuery="insert into Comment(userId,boardId,comment) values(?,?,?)";
        Object[] getCommentParams = new Object[]{postCommentReq.getUserId(),postCommentReq.getBoardId(),postCommentReq.getComment(),};
        this.jdbcTemplate.update(postCommentQuery,getCommentParams);
    }

    public void postCommentLike(Long userId, Long commentId) {
        String postCommentQuery="insert into CommentLike(userId,commentId) values(?,?)";
        Object[] getCommentParams = new Object[]{userId,commentId};
        this.jdbcTemplate.update(postCommentQuery,getCommentParams);
    }

    public int checkComment(Long commentId) {
        String checkIdQuery = "select exists(select id from Comment where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                commentId);
    }

    public int checkBoard(Long boardId) {
        String checkIdQuery = "select exists(select id from CommentLike where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                boardId);
    }

    public int checkUser(Long id){
        String checkIdQuery = "select exists(select id from User where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                id);
    }

    public void deleteCommentLike(Long userId, Long commentId) {
        String deleteLikeQuery="delete from CommentLike where userId=? and commentId=?";
        Object[] deleteLikeParams = new Object[]{userId,commentId};

        this.jdbcTemplate.update(deleteLikeQuery,deleteLikeParams);
    }

    public void postReComment(PostReCommentReq postCommentReq) {
        String postCommentQuery="insert into ReComment(userId,commentId,reComment) values(?,?,?)";
        Object[] getCommentParams = new Object[]{postCommentReq.getUserId(),postCommentReq.getCommentId(),postCommentReq.getReComment()};
        this.jdbcTemplate.update(postCommentQuery,getCommentParams);
    }

    public void postReCommentLike(Long userId, Long reCommentId) {
        String postCommentLikeQuery="insert into ReCommentLike(userId,reCommentId) values(?,?)";
        Object[] getCommentLikeParams = new Object[]{userId,reCommentId};
        this.jdbcTemplate.update(postCommentLikeQuery,getCommentLikeParams);
    }

    public int checkReComment(Long reCommentId) {
        String checkIdQuery = "select exists(select id from ReComment where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                reCommentId);
    }

    public void deleteReCommentLike(Long userId, Long reCommentId) {
        String deleteLikeQuery="delete from ReCommentLike where userId=? and reCommentId=?";
        Object[] deleteLikeParams = new Object[]{userId,reCommentId};

        this.jdbcTemplate.update(deleteLikeQuery,deleteLikeParams);
    }

    public List<GetReCommentReq> getReComment(Long userId, Long commentId, int paging) {
        String getReCommentQuery="select U.id                                                                'userId',\n" +
                "       U.profileImg                                                        'profileImgUrl',\n" +
                "       U.userId                                                            'userLoginId',\n" +
                "       C.id'reCommentId',\n" +
                "       reComment,\n" +
                "               case\n" +
                "                   when TIMESTAMPDIFF(WEEK, C.createdDate, now())>1\n" +
                "                   then concat(TIMESTAMPDIFF(WEEK, C.createdDate, now()),'주 전')\n" +
                "                   when TIMESTAMPDIFF(minute, C.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(second, C.createdDate, now()),'초 전')\n" +
                "                   when TIMESTAMPDIFF(hour, C.createdDate, now()) > 24\n" +
                "                       then concat(TIMESTAMPDIFF(DAY, C.createdDate, now()), '일 전')\n" +
                "                   when TIMESTAMPDIFF(hour, C.createdDate, now()) < 1\n" +
                "                       then concat(TIMESTAMPDIFF(minute, C.createdDate, now()), '분 전')\n" +
                "                   when TIMESTAMPDIFF(hour, C.createdDate, now()) < 24\n" +
                "                       then concat(TIMESTAMPDIFF(hour, C.createdDate, now()), '시간 전')\n" +
                "                   end as 'reCommentTime',\n" +
                "       (select count(CL.id) from ReCommentLike CL where CL.reCommentId = C.id) 'likeCnt',\n" +
                "       (select exists(select id from ReCommentLike CL where CL.reCommentId=C.id and CL.userId=?))'likeCheck'\n" +
                "from User U\n" +
                "         join ReComment C on C.userId = U.id\n" +
                "where C.commentId = ? order by C.createdDate desc limit ?,?;\n";
        Object[] getReComment = new Object[]{
                userId,commentId,(paging-1)*10,paging*10
        };

        return this.jdbcTemplate.query(getReCommentQuery,
                (rs,row) ->new GetReCommentReq(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userLoginId"),
                        rs.getLong("reCommentId"),
                        rs.getString("reComment"),
                        rs.getString("reCommentTime"),
                        rs.getInt("likeCnt"),
                        rs.getInt("likeCheck")
                ),getReComment);
    }
}

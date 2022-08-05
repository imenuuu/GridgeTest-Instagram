package com.example.demo.src.follow;

import com.example.demo.src.follow.model.GetFollowKeepRes;
import com.example.demo.src.user.model.GetProfileBoardRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FollowDao {
    private JdbcTemplate jdbcTemplate;

    List<GetProfileBoardRes> getProfileBoardRes;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createFollow(Long userId, Long followUserId) {
        String createFollowQuery="insert into Following(userId,followUserId) values(?,?) ";
        Object[] createFollowParams=new Object[]{
                userId,followUserId
        };

        this.jdbcTemplate.update(createFollowQuery,createFollowParams);

    }

    public void requestFollow(Long userId, Long followUserId) {
        String requestFollowQuery="insert into FollowRequest(userId,requestUserId) values(?,?) ";
        Object[] requestFollowParams=new Object[]{
                userId,followUserId
        };

        this.jdbcTemplate.update(requestFollowQuery,requestFollowParams);
    }

    public String getUserPublic(Long followUserId) {
        String getUserPublicQuery="select userPublic from User where id=?";
        return this.jdbcTemplate.queryForObject(getUserPublicQuery,
                String.class,followUserId);
    }
    public void deleteRequestFollow(Long userId,Long followUserId){
        String deleteRequestFollowQuery="delete from FollowRequest where userId = ? and requestUserId=? ";
        Object[] deleteRequestFollowParams=new Object[]{
                userId,followUserId
        };
        this.jdbcTemplate.update(deleteRequestFollowQuery,deleteRequestFollowParams);
    }

    public void unFollow(Long userId, Long blockUserId) {
        String unFollowQuery="delete from Following where userId =? and followUserId=?";
        Object[] unFollowParams=new Object[]{
                userId,blockUserId
        };

        this.jdbcTemplate.update(unFollowQuery,unFollowParams);
    }

    public int checkUser(Long id){
        String checkIdQuery = "select exists(select id from User where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                id);
    }

    public Long getRequestUserId(Long requestId) {
        String getRequestUserIdQuery = "select userId from FollowRequest where id=?";
        return this.jdbcTemplate.queryForObject(getRequestUserIdQuery,
                Long.class,
                requestId);
    }

    public void deleteRequest(Long requestId) {
        String deleteRequestQuery="delete from FollowRequest where id=?";
        this.jdbcTemplate.update(deleteRequestQuery,requestId);
    }

    public Long getUserId(Long requestId) {
        String getRequestUserIdQuery = "select requestUserId from FollowRequest where id=?";
        return this.jdbcTemplate.queryForObject(getRequestUserIdQuery,
                Long.class,
                requestId);
    }

    public List<GetFollowKeepRes> getFollowKeep(Long userId) {
        String getFollowKeepQuery="select FR.id'requestId',U.id'userId',U.profileImg'profileImgUrl',U.userId'userLoginId',U.name'userName'\n" +
                "from FollowRequest FR join User U on FR.userId= U.id\n" +
                "where FR.requestUserId = ? and FR.status='TRUE'";
        return this.jdbcTemplate.query(getFollowKeepQuery,
                (rs,rowNum) ->new GetFollowKeepRes(
                        rs.getLong("requestId"),
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userLoginId"),
                        rs.getString("userName")

                ),userId);
    }

    public int checkRequest(Long requestId) {
        String checkIdQuery = "select exists(select id from FollowRequest where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, requestId);
    }

    public int checkFollow(Long userId, Long followUserId) {
        String checkIdQuery = "select exists(select id from Following where userId=? and followUserId=?)";
        Object[] checkIdParams = new Object[]{
            userId,followUserId
        };
        return this.jdbcTemplate.queryForObject(checkIdQuery,int.class,checkIdParams);
    }

    public int checkFollowRequest(Long userId, Long followUserId) {
        String checkIdQuery = "select exists(select id from FollowRequest where userId=? and requestUserId=?)";
        Object[] checkIdParams = new Object[]{
                userId,followUserId
        };
        return this.jdbcTemplate.queryForObject(checkIdQuery,int.class,checkIdParams);
    }

    public Long getFollowRequestId(Long userId, Long followUserId) {
        String checkIdQuery = "select  id from FollowRequest where userId=? and requestUserId=?";
        Object[] checkIdParams = new Object[]{
                userId,followUserId
        };
        return this.jdbcTemplate.queryForObject(checkIdQuery,Long.class,checkIdParams);

    }
}

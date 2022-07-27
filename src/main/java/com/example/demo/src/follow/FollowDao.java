package com.example.demo.src.follow;

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
}

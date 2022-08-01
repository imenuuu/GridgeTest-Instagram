package com.example.demo.src.admin;

import com.example.demo.src.admin.model.GetUserReq;
import com.example.demo.src.admin.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AdminDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(GetUserReq getUserReq) {
        String getUsersQuery=String.format("select id'userId',userId'userLoginId',phoneNumber,logInDate from User " +
                "where id>0 %s %s %s %s order by createdDate desc limit ?,?",getUserReq.getUserIdQuery(),getUserReq.getNameQuery(),getUserReq.getStatusQuery(),getUserReq.getDateQuery());
        System.out.println(getUsersQuery);
        Object[] paging=new Object[]{
                (getUserReq.getPaging()-1)*10,(getUserReq.getPaging())*10
        };
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum)->new GetUserRes(
                        rs.getLong("userId"),
                        rs.getString("userLoginId"),
                        rs.getString("phoneNumber"),
                        rs.getDate("logInDate")
                ),paging);

    }
}

package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    List<GetProfileBoardRes> getProfileBoardRes;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public List<GetUserRes> getUsersByEmail(String email){
        String getUsersByphoneNumberQuery = "select * from User where phoneNumber =?";
        String getUsersByphoneNumberParams = email;
        return this.jdbcTemplate.query(getUsersByphoneNumberQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUsersByphoneNumberParams);
    }

    public GetUserRes getUser(int userId){
        String getUserQuery = "select * from User where id = ?";
        int getUserParams = userId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUserParams);
    }
    

    public Long createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (userId,birth,password,name,phoneNumber) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserId(), postUserReq.getBirth(), postUserReq.getPassword(), postUserReq.getName(),postUserReq.getPhoneNumber()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,Long.class);
    }

    public int checkId(String id){
        String checkIdQuery = "select exists(select userId from User where userId = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                id);
    }
    public int checkUser(Long id){
        String checkIdQuery = "select exists(select id from User where id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                id);
    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set userId = ? where id = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserId()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select id,userId,name,password from User where userId = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getLong("id"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("password")
                ),
                getPwdParams
                );

    }


    public List<GetUserRes> getUsers() {
        return null;
    }

    public List<GetMyProfileRes> getMyProfile(Long userId) {
        String getMyProfileQuery = "select U.id         'userId',\n" +
                "       U.userId     'userName',\n" +
                "       U.profileImg 'profileImgUrl',\n" +
                "       U.name,\n" +
                "       U.introduce,\n" +
                "       U.webSite,\n" +
                "       count(B.id)'boardCnt',\n" +
                "       (select count(F.followUserId) from Following F\n" +
                "        where F.followUserId = U.id and F  .userId != U.id)'followerCnt',\n" +
                "       (select count(F.userId) from Following F where F.userId=U.id)'followingCnt'\n" +
                "from User U\n" +
                "left join Board B on B.userId=U.id\n" +
                "where U.id = ?";
        String getProfileBoardQuery="select B.id 'boardId', " +
                "(select BI.boardImgurl from BoardImg BI where BI.boardId=B.id order by BI.id asc limit 1 ) as 'imgurl'\n" +
                "from Board B\n" +
                "where B.userId=?";
        return this.jdbcTemplate.query(getMyProfileQuery,
                (rs,rowNum) ->new GetMyProfileRes(
                        rs.getLong("userId"),
                        rs.getString("userName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("introduce"),
                        rs.getString("website"),
                        rs.getInt("boardCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt"),
                        getProfileBoardRes=this.jdbcTemplate.query(getProfileBoardQuery,
                                (rk,rownum)->new GetProfileBoardRes(
                                        rk.getLong("boardId"),
                                        rk.getString("imgUrl")
                                ),userId)
                ),userId);
    }

    public List<GetUserProfileRes> getUserProfile(Long userId, Long targetId) {
        String getMyProfileQuery = "select U.userPublic," +
                "       U.id         'userId',\n" +
                "       U.userId     'userName',\n" +
                "       U.profileImg 'profileImgUrl',\n" +
                "       U.name,\n" +
                "       U.introduce,\n" +
                "       U.webSite,\n" +
                "       count(B.id)'boardCnt',\n" +
                "       (select count(F.followUserId) from Following F\n" +
                "        where F.followUserId = U.id and F  .userId != U.id)'followerCnt',\n" +
                "       (select count(F.userId) from Following F where F.userId=U.id)'followingCnt',\n" +
                "       (select exists(select F.followUserId from Following F where F.userId=? and F.followUserId=U.id ))'followCheck'" +
                "from User U\n" +
                "left join Board B on B.userId=U.id\n" +
                "where U.id = ?";
        String getProfileBoardQuery="select B.id 'boardId', " +
                "(select BI.boardImgurl from BoardImg BI where BI.boardId=B.id order by BI.id asc limit 1 ) as 'imgurl'\n" +
                "from Board B\n" +
                "where B.userId=?";
        return this.jdbcTemplate.query(getMyProfileQuery,
                (rs,rowNum) ->new GetUserProfileRes(
                        rs.getString("userPublic"),
                        rs.getLong("userId"),
                        rs.getString("userName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("introduce"),
                        rs.getString("website"),
                        rs.getInt("boardCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt"),
                        rs.getInt("followCheck"),
                        getProfileBoardRes=this.jdbcTemplate.query(getProfileBoardQuery,
                                (rk,rownum)->new GetProfileBoardRes(
                                        rk.getLong("boardId"),
                                        rk.getString("imgUrl")
                                ),userId)
                ),userId,targetId);
    }

    public int getUserKakaoExists(String email) {
        String checkIdQuery = "select exists(select id from KakaoUser where kakaoEmail = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class, email);
    }


    public Long getIdByKakaoEmail(String email) {
        String getIdByEmail="select userId from KakaoUser where kakaoEmail=?";
        return this.jdbcTemplate.queryForObject(getIdByEmail,Long.class,email);
    }

    public Long createUserByKakao(PostKakaoUserReq postKakaoUserReq) {
        String createUserQuery = "insert into User (userId,birth,name,phoneNumber) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postKakaoUserReq.getUserId(), postKakaoUserReq.getBirth(), postKakaoUserReq.getName(),postKakaoUserReq.getPhoneNumber()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,Long.class);
    }

    public void createKakaoUser(KakaoInfo kakaoInfo, Long userId) {
        String postUserKakaoQuery="insert into KakaoUser(userId,kakaoName, kakaoId, kakaoEmail) values(?,?,?,?)";
        Object[] postUserKakaoParams=new Object[]{
                userId,kakaoInfo.getKakaoName(), kakaoInfo.getKakaoId(), kakaoInfo.getKakaoEmail()
        };
        this.jdbcTemplate.update(postUserKakaoQuery,postUserKakaoParams);
    }
    public String getUserPublic(Long followUserId) {
        String getUserPublicQuery="select userPublic from User where id=?";
        return this.jdbcTemplate.queryForObject(getUserPublicQuery,
                String.class,followUserId);
    }

    public void userBlock(Long userId, Long blockUserId) {
        String userBlockQuery="insert into UserBlock(userId,blockedUserId) values(?,?)";
        Object[] userBlockParams=new Object[]{
                userId,blockUserId
        };

        this.jdbcTemplate.update(userBlockQuery,userBlockParams);
    }

    public int checkBlock(Long userId, Long profileUserId) {
        String checkBlockQuery="select exists(select id from UserBlock where userId=? and blockedUserId=?)";
        Object[] checkBlockParams=new Object[]{userId,profileUserId};
        return this.jdbcTemplate.queryForObject(checkBlockQuery,int.class,checkBlockParams);
    }

    public void modifyProfileImg(Long userId, PatchProfileImgReq patchProfileImgReq) {
        String modifyProfileImgQuery="update User set profileImg=? where id = ? ";
        Object[] modifyProfileImgParam=new Object[]{patchProfileImgReq.getProfileImgUrl(),userId};
        this.jdbcTemplate.update(modifyProfileImgQuery,modifyProfileImgParam);
    }

    public void modifyProfile(Long userId, PatchProfileReq patchProfileReq) {
        String modifyProfileImgQuery="update User set name=?, userId=? , webSite=?, webSite=? , introduce=? where id = ? ";
        Object[] modifyProfileImgParam=new Object[]{patchProfileReq.getName(),patchProfileReq.getUserId(),patchProfileReq.getWebsite(),patchProfileReq.getIntroduce(),userId};
        this.jdbcTemplate.update(modifyProfileImgQuery,modifyProfileImgParam);
    }

    public List<GetClosedProfileRes> getCloesdProfile(Long userId, Long profileUserId) {
            String getMyProfileQuery = "select U.userPublic," +
                    "       U.id         'userId',\n" +
                    "       U.userId     'userName',\n" +
                    "       U.profileImg 'profileImgUrl',\n" +
                    "       U.name,\n" +
                    "       U.introduce,\n" +
                    "       U.webSite,\n" +
                    "       count(B.id)'boardCnt',\n" +
                    "       (select count(F.followUserId) from Following F\n" +
                    "        where F.followUserId = U.id and F  .userId != U.id)'followerCnt',\n" +
                    "       (select count(F.userId) from Following F where F.userId=U.id)'followingCnt',\n" +
                    "       (select exists(select F.followUserId from Following F where F.userId=? and F.followUserId=U.id ))'followCheck'" +
                    "from User U\n" +
                    "left join Board B on B.userId=U.id\n" +
                    "where U.id = ?";
            return this.jdbcTemplate.query(getMyProfileQuery,
                    (rs,rowNum) ->new GetClosedProfileRes(
                            rs.getLong("userId"),
                            rs.getString("userName"),
                            rs.getString("profileImgUrl"),
                            rs.getString("name"),
                            rs.getString("introduce"),
                            rs.getString("website"),
                            rs.getInt("boardCnt"),
                            rs.getInt("followerCnt"),
                            rs.getInt("followingCnt"),
                            rs.getInt("followCheck")
                    ),userId,profileUserId);
        }
    }

package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    List<GetProfileBoardRes> getProfileBoard;
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
    public User getPhoneNumberPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select id,userId,name,password from User where phoneNumber = ?";
        String getPwdParams = postLoginReq.getId();

        System.out.println(getPwdParams);
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
                "       U.userId     'userLoginId',\n" +
                "       U.profileImg 'profileImgUrl',\n" +
                "       U.name,\n" +
                "       U.introduce,\n" +
                "       U.webSite,\n" +
                "       count(B.id)'boardCnt',\n" +
                "       (select count(F.followUserId) from Following F\n" +
                "        where F.followUserId = U.id and F.userId != U.id)'followerCnt',\n" +
                "       (select count(F.userId) from Following F where F.userId=U.id)'followingCnt'\n" +
                "from User U\n" +
                "left join Board B on B.userId=U.id and B.status='TRUE' and B.suspensionStatus='TRUE'\n" +
                "where U.id = ?";
        String getProfileBoardQuery="select B.id 'boardId', " +
                "(select BI.boardImgurl from BoardImg BI where BI.boardId=B.id order by BI.id asc limit 1 ) as 'imgurl'\n" +
                "from Board B\n" +
                "where B.userId=?";
        return this.jdbcTemplate.query(getMyProfileQuery,
                (rs,rowNum) ->new GetMyProfileRes(
                        rs.getLong("userId"),
                        rs.getString("userLoginId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("introduce"),
                        rs.getString("website"),
                        rs.getInt("boardCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt"),
                        getProfileBoard=this.jdbcTemplate.query(getProfileBoardQuery,
                                (rk,rownum)->new GetProfileBoardRes(
                                        rk.getLong("boardId"),
                                        rk.getString("imgUrl")
                                ),userId)
                ),userId);
    }

    public List<GetUserProfileRes> getUserProfile(Long userId, Long targetId) {
        String getMyProfileQuery = "select U.userPublic," +
                "       U.id         'userId',\n" +
                "       U.userId     'userLoginId',\n" +
                "       U.profileImg 'profileImgUrl',\n" +
                "       U.name,\n" +
                "       U.introduce,\n" +
                "       U.webSite,\n" +
                "       count(B.id)'boardCnt',\n" +
                "       (select count(F.followUserId) from Following F\n" +
                "        where F.followUserId = U.id and F.userId != U.id and F.status='TRUE')'followerCnt',\n" +
                "       (select count(F.userId) from Following F where F.userId=U.id and F.status='TRUE' )'followingCnt',\n" +
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
                        rs.getString("userLoginId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("introduce"),
                        rs.getString("website"),
                        rs.getInt("boardCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt"),
                        rs.getInt("followCheck"),
                        getProfileBoard=this.jdbcTemplate.query(getProfileBoardQuery,
                                (rk,rownum)->new GetProfileBoardRes(
                                        rk.getLong("boardId"),
                                        rk.getString("imgUrl")
                                ),targetId)
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
        String modifyProfileImgQuery="update User set name=?, userId=? , webSite=? , introduce=? where id = ? ";
        Object[] modifyProfileImgParam=new Object[]{patchProfileReq.getName(),patchProfileReq.getUserId(),patchProfileReq.getWebsite(),patchProfileReq.getIntroduce(),userId};
        this.jdbcTemplate.update(modifyProfileImgQuery,modifyProfileImgParam);
    }

    public List<GetClosedProfileRes> getCloesdProfile(Long userId, Long profileUserId) {
            String getMyProfileQuery = "select U.userPublic," +
                    "       U.id         'userId',\n" +
                    "       U.userId     'userLoginId',\n" +
                    "       U.profileImg 'profileImgUrl',\n" +
                    "       U.name,\n" +
                    "       U.introduce,\n" +
                    "       U.webSite,\n" +
                    "       count(B.id)'boardCnt',\n" +
                    "       (select count(F.followUserId) from Following F\n" +
                    "        where F.followUserId = U.id and F.userId != U.id and F.status='TRUE')'followerCnt',\n" +
                    "       (select count(F.userId) from Following F where F.userId=U.id and F.status='TRUE')'followingCnt',\n" +
                    "       (select exists(select F.followUserId from Following F where F.userId=? and F.followUserId=U.id ))'followCheck'" +
                    "from User U\n" +
                    "left join Board B on B.userId=U.id  and B.status='TRUE' and B.suspensionStatus='FALSE' \n" +
                    "where U.id = ?";
            return this.jdbcTemplate.query(getMyProfileQuery,
                    (rs,rowNum) ->new GetClosedProfileRes(
                            rs.getLong("userId"),
                            rs.getString("userLoginId"),
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

    public int checkUserPhoneNumber(PatchPasswordRes patchPasswordRes) {
        String checkUserPhoneNubmerQuery="select exists(select id from User where userId=? and phoneNumber=?)";
        Object[] checkUserPhoneNumberParams = new Object[]{patchPasswordRes.getUserId(),patchPasswordRes.getPhoneNumber()};
        return this.jdbcTemplate.queryForObject(
                checkUserPhoneNubmerQuery,int.class,checkUserPhoneNumberParams);
    }

    public void modifyPassword(PatchPasswordRes patchPasswordRes) {
        String modifyPasswordQuery="update User set password=? where userId=? and phoneNumber=? ";
        Object[] modifyPasswordParam=new Object[]{patchPasswordRes.getPassword(),patchPasswordRes.getUserId(),patchPasswordRes.getPhoneNumber()};
        this.jdbcTemplate.update(modifyPasswordQuery,modifyPasswordParam);
    }

    public void modifyPublicTrue(Long userId) {
        String modifyPublicQuery="update User set userPublic='TRUE' where id=?";
        this.jdbcTemplate.update(modifyPublicQuery,userId);
    }

    public void modifyPublicFalse(Long userId) {
        String modifyPublicQuery="update User set userPublic='FALSE' where id=?";
        this.jdbcTemplate.update(modifyPublicQuery,userId);
    }

    public int checkFollow(Long userId, Long profileUserId) {
        String checkFollowQuery = "select exists(select id from Following where userId=? and followUserId=?)";
        Object[] checkFollowParam = new Object[]{userId,profileUserId};
        return this.jdbcTemplate.queryForObject(checkFollowQuery,
                int.class, checkFollowParam);
    }

    public void logIn(Long userId) {
        String logInQuery = "update User set logInDate=now() where id=?\n";
        this.jdbcTemplate.update(logInQuery,userId);
    }


    public int checkPhoneNumber(String phoneNumber) {
        String checkQuery = "select exists(select id from User where phoneNumber=?)";
        return this.jdbcTemplate.queryForObject(checkQuery,
                int.class, phoneNumber);
    }

    public void updateLogInDate(Long userId) {
        String updateLogInDateQuery = "update User set logInDate=now() where id=?";
        this.jdbcTemplate.update(updateLogInDateQuery,userId);
    }


    public void updateAllStatus(Long userId) throws SQLException {
        String updateUserQuery = "update User set userStatus='FALSE' where id=?";
        String updateBoardQuery = "update Board set status='FALSE' where userId=?";
        String updateBoardLikeQuery = "update BoardLike set status='FALSE' where userId=?";
        String updateBoardReportQuery = "update BoardReport set status='FALSE' where userId=?";
        String updateCommentQuery = "update Comment set status='FALSE' where userId=?";
        String updateCommentLikeQuery = "update CommentLike set status='FALSE' where userId=?";
        String updateFollowingQuery = "update Following set status='FALSE' where userId=?";
        String updateFollowingRequestQuery = "update FollowRequest set status='FALSE' where userId=?";
        String updateKakaoUserQuery = "update KakaoUser set status='FALSE' where userId=?";
        String updateMessageQuery = "update Message set status='FALSE' where userId=?";
        String updateMessageLikeQuery = "update MessageLike set status='FALSE' where userId=? ";
        String updateReCommentQuery = "update ReComment set status='FALSE' where userId=?";
        String updateReCommentLikeQuery = "update ReCommentLike set status='FALSE' where userId=?";

        TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 작업 초기화
        Connection conn = DataSourceUtils.getConnection(dataSource);
        conn.setAutoCommit(false);
        try {
            this.jdbcTemplate.update(updateUserQuery, userId);
            this.jdbcTemplate.update(updateBoardQuery, userId);
            this.jdbcTemplate.update(updateBoardLikeQuery, userId);
            this.jdbcTemplate.update(updateBoardReportQuery, userId);
            this.jdbcTemplate.update(updateCommentQuery, userId);
            this.jdbcTemplate.update(updateCommentLikeQuery, userId);
            this.jdbcTemplate.update(updateFollowingQuery, userId);
            this.jdbcTemplate.update(updateFollowingRequestQuery, userId);
            this.jdbcTemplate.update(updateKakaoUserQuery, userId);
            this.jdbcTemplate.update(updateMessageQuery, userId);
            this.jdbcTemplate.update(updateMessageLikeQuery, userId);
            this.jdbcTemplate.update(updateReCommentQuery, userId);
            this.jdbcTemplate.update(updateReCommentLikeQuery, userId);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }

    }




}


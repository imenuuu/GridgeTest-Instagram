package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
        try{
            List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
                    }


    public GetUserRes getUser(int userId) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userId);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkId(String Id) throws BaseException{
        try{
            return userDao.checkId(Id);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUser(Long userId) throws BaseException{
        try{
            return userDao.checkUser(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao.getPwd(postLoginReq);
        String encryptPwd;
        try {
            encryptPwd=new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            Long userId = user.getId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

    public PostLoginRes phoneLogin(PostLoginReq postLoginReq) throws BaseException {
        User user = userDao.getPhoneNumberPwd(postLoginReq);
        String encryptPwd;
        try {
            encryptPwd=new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            Long userId = user.getId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public List<GetMyProfileRes> getMyProfile(Long userId) {
        List<GetMyProfileRes> getMyProfileRes=userDao.getMyProfile(userId);
        return getMyProfileRes;
    }

    public List<GetUserProfileRes> getUserProfile(Long userId, Long targetId) {
        List<GetUserProfileRes> getUserProfileRes=userDao.getUserProfile(userId,targetId);
        return getUserProfileRes;
    }

    public int checkKakaoUser(String email) throws BaseException {
        try {
            return userDao.getUserKakaoExists(email);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logInKakao(String k_email) throws BaseException {
        if (userDao.getUserKakaoExists(k_email) == 1) {
            Long userIdx = userDao.getIdByKakaoEmail(k_email);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }
    public String getUserPublic(Long followUserId) {
        String userPublic=userDao.getUserPublic(followUserId);
        return userPublic;
    }


    public int checkBlock(Long profileUserId, Long userId) throws BaseException {
        try{
            return userDao.checkBlock(userId,profileUserId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetClosedProfileRes> getCloesdProfile(Long userId, Long profileUserId) {
        List<GetClosedProfileRes> getCloesdProfile=userDao.getCloesdProfile(userId,profileUserId);
        return getCloesdProfile;
    }

    public int checkUserPhoneNumber(PatchPasswordRes patchPasswordRes) throws BaseException {
        try{
            return userDao.checkUserPhoneNumber(patchPasswordRes);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPublicFalse(Long userId) throws BaseException {
        try{
            userDao.modifyPublicFalse(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPublicTrue(Long userId) throws BaseException {
        try{
            userDao.modifyPublicTrue(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkFollow(Long userId, Long profileUserId) throws BaseException {
        try{
            return userDao.checkFollow(userId, profileUserId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkPhoneNumber(String phoneNumber) throws BaseException {
        try {
            return userDao.checkPhoneNumber(phoneNumber);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

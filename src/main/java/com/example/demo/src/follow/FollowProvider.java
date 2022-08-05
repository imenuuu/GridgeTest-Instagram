package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.follow.model.GetFollowKeepRes;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.NOT_EXIST_FOLLOW;

@Service
public class FollowProvider {
    private final FollowDao followDao;

    public FollowProvider(FollowDao followDao) {
        this.followDao = followDao;
    }

    public String getUserPublic(Long followUserId) throws BaseException {
        try {
            String userPublic = followDao.getUserPublic(followUserId);
            return userPublic;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        }
    public int checkUser(Long userId) throws BaseException{
        try{
            return followDao.checkUser(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Long getRequestUserId(Long requestId) throws BaseException {
            try{
                return followDao.getRequestUserId(requestId);
            } catch (Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
    }

    public Long getUserId(Long requestId) throws BaseException {
        try {
            return followDao.getUserId(requestId);
        } catch (Exception exception){
        throw new BaseException(DATABASE_ERROR);
    }
    }

    public List<GetFollowKeepRes> getFollowKeep(Long userId) {
        return followDao.getFollowKeep(userId);
    }

    public int checkRequest(Long requestId) throws BaseException {
        try {
            return followDao.checkRequest(requestId);
        } catch (Exception exception){
        throw new BaseException(DATABASE_ERROR);
    }
    }

    public int checkFollow(Long userId, Long followUserId) throws BaseException {
        try {
            return followDao.checkFollow(userId, followUserId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkFollowRequest(Long userId, Long followUserId) throws BaseException {
        try{
            return followDao.checkFollowRequest(userId, followUserId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Long getFollowRequestId(Long userId, Long followUserId) throws BaseException {
        try{
            return followDao.getFollowRequestId(userId, followUserId);
        }catch(Exception exception){
            throw new BaseException(NOT_EXIST_FOLLOW);
        }
    }
}
